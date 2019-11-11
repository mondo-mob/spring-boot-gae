package org.springframework.contrib.gae.datastore;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.util.Preconditions;

import java.io.IOException;

public class EditableMockHttpTransport extends MockHttpTransport {

	private MockLowLevelHttpResponse nextResponse;
	private MockLowLevelHttpRequest lastRequest;

	public void setNextResponse(MockLowLevelHttpResponse nextResponse) {
		this.nextResponse = nextResponse;
	}

	public MockLowLevelHttpRequest getLastRequest() {
		return lastRequest;
	}

	@Override
	public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
		Preconditions.checkArgument(supportsMethod(method), "HTTP method %s not supported", method);
		MockLowLevelHttpRequest request = new MockLowLevelHttpRequest(url);

		if (nextResponse != null) {
			request.setResponse(nextResponse);
			nextResponse = null;
		}
		lastRequest = request;
		return request;
	}

	public void reset() {
		this.nextResponse = null;
		this.lastRequest = null;
	}
}
