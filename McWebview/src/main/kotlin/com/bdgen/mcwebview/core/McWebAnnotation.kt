package com.bdgen.mcwebview.core

import android.util.Log
import com.bdgen.mcwebview.result.McWebFailure
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.FUNCTION) // 프로퍼티와 함수에 적용 가능하도록 설정
@Retention(AnnotationRetention.RUNTIME) // 런타임에 어노테이션 정보를 가져올 수 있도록 설정
annotation class McWebMethod (val name: String = "") // 선택적으로 함수의 이름을 지정할 수 있도록 합니다.

object McFunctionRegistrar {
    fun registerFunctions(plugin: McWebPlugin): HashMap<String, McWebFunction> {
        Log.d("McFunctionRegistrar", "Starting function registration for plugin: ${plugin::class.simpleName}")

        val registeredFunctions = HashMap<String, McWebFunction>()

        plugin::class.declaredMemberFunctions.forEach { kFunction ->
            kFunction.findAnnotation<McWebMethod>()?.let { annotation ->
                val functionName = annotation.name.ifEmpty { kFunction.name }

                // kFunction.isAccessible = true // private 함수 접근 시 필요
                val functionLambda: (String, HashMap<String, *>?) -> Unit = lambda@{ callbackId, params ->
                    try {
                        val instanceParameter = kFunction.parameters.firstOrNull { it.kind == KParameter.Kind.INSTANCE }
                        val valueParameters = kFunction.parameters.filter { it.kind == KParameter.Kind.VALUE }

                        val callArguments = mutableMapOf<KParameter, Any?>()
                        instanceParameter?.let { callArguments[it] = plugin }

                        val allParamsMatched = mappingParam(callbackId, params, valueParameters, callArguments)

                        if (allParamsMatched && valueParameters.size == callArguments.filterKeys { it.kind == KParameter.Kind.VALUE }.size) {
                            // 모든 필수 값 파라미터가 callArguments에 채워졌거나, 파라미터 자체가 없는 경우
                            kFunction.callBy(callArguments)
                            // 성공 응답은 함수 내부에서 처리하거나 여기서 기본 성공 처리 가능
                            // plugin.sendResult(McResponseStatus.success, callbackId, mapOf("message" to "${kFunction.name} executed successfully via reflection."))
                        } else if (!allParamsMatched) {
                            throw IllegalArgumentException("Failed to map all required parameters for ${kFunction.name} from provided params. Check logs.")
                        } else {
                            // 이 경우는 valueParameters는 있는데 callArguments에 값 파라미터가 충분히 안 채워진 경우
                            // (예: 모든 파라미터가 optional 이고 params에 아무것도 안 온 경우 kFunction.callBy(callArguments) 시도)
                            // 또는 valueParameters가 아예 없는 경우
                            if(valueParameters.isEmpty()){
                                kFunction.callBy(callArguments)
                            } else {
                                Log.d("McFunctionRegistrar", "Not all parameters for ${kFunction.name} were present in params, but attempting call (likely optional params).")
                                try {
                                    kFunction.callBy(callArguments) // 선택적 파라미터에 의존
                                } catch (e: Exception) {
                                    Log.e("McFunctionRegistrar", "Error calling ${kFunction.name} with potentially missing optional params.", e)
                                    throw IllegalArgumentException("Error calling ${kFunction.name} with optional parameters: ${e.message}", e)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("McFunctionRegistrar", "Error executing annotated function ${kFunction.name} in plugin ${plugin::class.simpleName}", e)
                        plugin.sendResult(McResponseStatus.failure, callbackId, McWebFailure("Error in ${kFunction.name}: ${e.localizedMessage ?: e.message ?: "Unknown error"}"))
                    }
                }

                val webFunction = McWebFunction(functionName, functionLambda)
                registeredFunctions[functionName] = webFunction // 로컬 맵에 추가
                Log.d("McFunctionRegistrar", "Registered function: $functionName (from method ${kFunction.name}) for plugin ${plugin::class.simpleName}")
            }
        }
        Log.d("McFunctionRegistrar", "Finished function registration for plugin ${plugin::class.simpleName}. Total functions: ${registeredFunctions.size}")
        return registeredFunctions
    }

    private fun mappingParam(callbackId:String, params: HashMap<String, *>?, valueParameters: List<KParameter>, callArguments: MutableMap<KParameter, Any?>): Boolean {
        val extendedParams = params?.toMutableMap() ?: mutableMapOf()
        // callbackId를 valueParameters 중 String? 타입이고 이름이 "callbackId"인 파라미터에 매핑 시도
        // 또는 개발자가 명시적으로 callbackId를 사용하도록 안내
        valueParameters.find { it.name == "callbackId" && it.type.classifier == String::class }
            ?.let { cbParam ->
                if (!extendedParams.containsKey("callbackId")) { // params에 callbackId가 없을 때만 명시적 callbackId 사용
                    extendedParams["callbackId"] = callbackId
                }
            }

        var allParamsMatched = true // 모든 필수 파라미터가 매칭되었는지 확인하는 플래그

        // 모든 값 파라미터에 대해 매핑 시도
        for (paramInfo in valueParameters) {
            val paramName = paramInfo.name
            val expectedType = paramInfo.type
            val expectedClass = expectedType.classifier as? KClass<*>

            if (extendedParams.containsKey(paramName)) {
                val valueFromParams = extendedParams[paramName]

                if (expectedClass != null && valueFromParams != null && expectedClass.isInstance(valueFromParams)) {
                    callArguments[paramInfo] = valueFromParams
                } else if (valueFromParams == null && expectedType.isMarkedNullable) {
                    callArguments[paramInfo] = null // Nullable 파라미터에 null 값 허용
                } else {
                    // 타입 불일치 또는 값 없음 (필수 파라미터인데 값이 null인 경우 등)
                    // Gson 등 변환 로직을 여기에 추가할 수 있음
                    // 예: if (valueFromParams is Map<*, *> && !expectedType.isMarkedNullable && expectedClass != null) {
                    // try convert map to DTO using Gson, then check isInstance and assign
                    // }
                    Log.w(
                        "McFunctionRegistrar",
                        "Parameter '$paramName' in params has incompatible type or value. " +
                                "Expected: $expectedType (Class: ${expectedClass?.simpleName}), Actual: ${valueFromParams?.let { it::class.simpleName } ?: "null"}, Value: $valueFromParams" +
                                " for function"
                    )
                    if (!expectedType.isMarkedNullable) { // Non-null 파라미터인데 문제가 생김
                        allParamsMatched = false
                        break // 필수 파라미터 매칭 실패 시 중단
                    } else {
                        // Nullable 파라미터인데 타입이 안맞지만, 일단 null로 시도 (또는 오류 처리)
                        callArguments[paramInfo] = null
                    }
                }
            } else {
                // extendedParams에 해당 파라미터 이름의 키가 없는 경우
                if (paramInfo.isOptional) {
                    // 선택적 파라미터는 값이 없어도 됨 (callBy가 알아서 처리)
                    // callArguments에 추가하지 않음
                } else if (expectedType.isMarkedNullable) {
                    // 필수지만 Nullable인 경우, 명시적으로 null 전달
                    callArguments[paramInfo] = null
                } else {
                    Log.w(
                        "McFunctionRegistrar",
                        "Required parameter '$paramName' not found in params for functione}"
                    )
                    allParamsMatched = false
                    break // 필수 파라미터 누락 시 중단
                }
            }
        }

        return allParamsMatched

    }
}