package com.vogella.http.netty;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vogella.common.core.service.ReactorHttpClient;

import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.ProxyProvider;
import reactor.netty.tcp.TcpClient;

@Component
public class NettyHttpClient implements ReactorHttpClient {

	private HttpClient client;
	private ObjectMapper om;
	private IProxyService proxyService;

	@Activate
	public void activate() {
		IProxyData[] proxyDataForHost = proxyService.getProxyData();
		String proxyHost = null;
		int proxyPort = -1;
		for (IProxyData data : proxyDataForHost) {
			if (data.getHost() != null) {
				System.setProperty("http.proxySet", "true");
				System.setProperty("http.proxyHost", data.getHost());
			}
			if (data.getHost() != null) {
				System.setProperty("http.proxyPort", String.valueOf(data.getPort()));
			}

			proxyHost = data.getHost();
			proxyPort = data.getPort();
		}

		TcpClient tcpClient = getTCPClient(proxyHost, proxyPort);

		client = HttpClient.from(tcpClient);
		om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private TcpClient getTCPClient(String proxyHost, int proxyPort) {
		if (proxyHost != null && proxyPort > -1) {
			TcpClient proxyClient = TcpClient.create(ConnectionProvider.elastic("Elastic-ConnectionProvider"))
					.proxy(typeSpec -> {
						typeSpec.type(ProxyProvider.Proxy.HTTP).host(proxyHost).port(proxyPort).build();
					});
			return proxyClient;
		}
		return TcpClient.create(ConnectionProvider.elastic("Elastic-ConnectionProvider"));
	}

	@Reference
	public void bindProxyService(IProxyService proxyService) {
		this.proxyService = proxyService;
	}

	public void unbindProxyService(IProxyService proxyService) {
		this.proxyService = null;
	}

	@Override
	public <T> Mono<T> get(String url, Class<T> responseType) {
		return client.headers(hb -> {
			hb.add("Accept", "application/json");
		}).get().uri(url).responseSingle((response, byteMono) -> {
			Mono<InputStream> ism = byteMono.asInputStream();

			return ism.map(is -> {
				try {
					return om.readValue(is, responseType);
				} catch (IOException e) {
					throw Exceptions.propagate(e);
				}
			});
		});
	}

	@Override
	public <T> Mono<T> get(String url, TypeReference<T> responseType) {
		return client.headers(hb -> {
			hb.add("Accept", "application/json");
		}).get().uri(url).responseSingle((response, byteMono) -> {
			Mono<InputStream> ism = byteMono.asInputStream();
			return ism.map(is -> {
				try {
					return om.readValue(is, responseType);
				} catch (IOException e) {
					throw Exceptions.propagate(e);
				}
			});
		});
	}

	@Override
	public <T> Mono<T> post(String url, Object body, Class<T> responseType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Mono<T> put(String url, Object body, Class<T> responseType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Mono<T> delete(String url, Object body, Class<T> responseType) {
		// TODO Auto-generated method stub
		return null;
	}

}
