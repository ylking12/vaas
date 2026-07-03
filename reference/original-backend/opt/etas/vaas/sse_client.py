import requests
import time
import json
from typing import Generator, Dict, Any, Optional


class SSEClient:
    """
    SSE (Server-Sent Events) 客户端实现
    """

    def __init__(self, url: str, headers: Optional[Dict[str, str]] = None,
                 params: Optional[Dict[str, Any]] = None, retry: int = 3000):
        """
        初始化SSE客户端

        Args:
            url: SSE API的URL
            headers: 请求头
            params: 请求参数
            retry: 连接断开后的重试时间(毫秒)
        """
        self.url = url
        self.headers = headers or {}
        self.params = params or {}
        self.retry = retry / 1000  # 转换为秒
        self.session = requests.Session()
        self.headers.setdefault('Accept', 'text/event-stream')
        self.headers.setdefault('Cache-Control', 'no-cache')

    def _parse_event(self, data: str) -> Dict[str, str]:
        """解析SSE事件数据"""
        event = {'data': ''}
        for line in data.splitlines():
            line = line.strip()
            if not line:
                continue

            # 处理注释行
            if line.startswith(':'):
                continue

            # 分割字段和值
            try:
                field, value = line.split(':', 1)
                field = field.strip()
                value = value.strip()

                # 特殊处理data字段，可能会有多行
                if field == 'data':
                    event['data'] = event['data'] + value + '\n'
                else:
                    event[field] = value
            except ValueError:
                continue

        # 移除data末尾的换行符
        if event['data'] and event['data'].endswith('\n'):
            event['data'] = event['data'][:-1]

        return event

    def connect(self) -> Generator[Dict[str, str], None, None]:
        """
        连接到SSE服务器并返回事件生成器

        Yields:
            解析后的事件字典，包含id、event、data等字段
        """
        last_event_id = None

        while True:
            try:
                # 如果有last_event_id，则添加到请求头中
                if last_event_id:
                    self.headers['Last-Event-ID'] = last_event_id

                # 发送请求
                with self.session.get(
                        self.url,
                        headers=self.headers,
                        params=self.params,
                        stream=True,
                        timeout=30
                ) as response:
                    response.raise_for_status()

                    # 处理响应流
                    buffer = ''
                    for chunk in response.iter_content(chunk_size=1024, decode_unicode=True):
                        if not chunk:
                            continue

                        buffer += chunk

                        # 按空行分割事件
                        while '\n\n' in buffer or '\r\n\r\n' in buffer:
                            # 检测换行符类型
                            if '\r\n\r\n' in buffer:
                                event_data, buffer = buffer.split('\r\n\r\n', 1)
                            else:
                                event_data, buffer = buffer.split('\n\n', 1)

                            # 解析事件
                            if event_data.strip():
                                event = self._parse_event(event_data)
                                if 'data' in event:
                                    # 保存last_event_id
                                    if 'id' in event:
                                        last_event_id = event['id']
                                    yield event

            except requests.exceptions.RequestException as e:
                print(f"连接错误: {e}")
                print(f"将在{self.retry}秒后重试...")
                time.sleep(self.retry)
                continue
            except Exception as e:
                print(f"未知错误: {e}")
                time.sleep(self.retry)
                continue


def example_usage():
    """SSE客户端使用示例"""
    # 替换为实际的SSE API URL
    sse_url = 'http://localhost:50410/spring/v1/stream_data'
    #sse_url = "https://qatest.chinanorth3.cloudapp.chinacloudapi.cn/spring/v1/stream_data"
    # 创建SSE客户端
    client = SSEClient(sse_url, headers={
        'Authorization': 'Bearer YOUR_API_KEY',
    })

    try:
        # 连接并处理事件
        for event in client.connect():
            print(f"收到事件: {event.get('event', 'message')}")
            try:
                # 尝试解析JSON数据
                data = json.loads(event.get('data', '{}'))
                print(f"数据: {json.dumps(data, indent=2)}")
            except json.JSONDecodeError:
                # 不是JSON数据，直接打印
                print(f"数据: {event.get('data')}")

    except KeyboardInterrupt:
        print("手动停止客户端")


if __name__ == "__main__":
    example_usage()

