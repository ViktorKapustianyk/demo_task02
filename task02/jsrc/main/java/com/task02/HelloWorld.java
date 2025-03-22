package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
		Map<String, Object> resultMap = new HashMap<>();

		Map<String, Object> requestContext = (Map<String, Object>) event.get("requestContext");

		if (requestContext != null) {
			Map<String, Object> http = (Map<String, Object>) requestContext.get("http");

			if (http != null) {
				String method = (String) http.get("method");
				String path = (String) http.get("path");

				if ("/hello".equals(path) && "GET".equalsIgnoreCase(method)) {
					resultMap.put("statusCode", 200);
					resultMap.put("message", "Hello from Lambda");
				} else {
					resultMap.put("statusCode", 400);
					resultMap.put("message", String.format(
							"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
							path != null ? path : "undefined",
							method != null ? method : "undefined"
					));
				}
				return resultMap;
			}
		}

		resultMap.put("statusCode", 400);
		resultMap.put("message", "Invalid request structure");
		return resultMap;
	}
}