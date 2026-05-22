import http from '@ohos.net.http';

const BASE = 'https://api.hongshu.ai/api';

export function getToken(): string {
  return AppStorage.get<string>('token') ?? '';
}

export function setToken(t: string): void {
  AppStorage.setOrCreate('token', t);
}

export function clearToken(): void {
  AppStorage.setOrCreate('token', '');
}

export async function apiGet(path: string, params?: Record<string, string>): Promise<Object> {
  let httpRequest = http.createHttp();
  let url = BASE + path;
  if (params) {
    const qs = Object.entries(params).map(([k,v]) => `${k}=${encodeURIComponent(v)}`).join('&');
    url += '?' + qs;
  }
  let response = await httpRequest.request(url, {
    method: http.RequestMethod.GET,
    header: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` }
  });
  return JSON.parse(response.result as string);
}

export async function apiPost(path: string, data: Object): Promise<Object> {
  let httpRequest = http.createHttp();
  let response = await httpRequest.request(BASE + path, {
    method: http.RequestMethod.POST,
    header: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}` },
    extraData: JSON.stringify(data)
  });
  return JSON.parse(response.result as string);
}
