import pymysql
import json
from datetime import datetime


def mysql_to_json(host, user, password, db_name, table_name, output_file):
    # 连接数据库
    conn = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=db_name,
        charset='utf8mb4'
    )

    try:
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            # 查询表数据
            cursor.execute(f"SELECT * FROM {table_name};")
            data = cursor.fetchall()

            # 处理datetime类型数据
            processed_data = []
            for row in data:
                processed_row = {}
                for key, value in row.items():
                    # 将datetime对象转换为字符串
                    if isinstance(value, datetime):
                        processed_row[key] = value.strftime('%Y-%m-%d %H:%M:%S.%f')
                    else:
                        processed_row[key] = value
                processed_data.append(processed_row)

            # 写入JSON文件
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(processed_data, f, ensure_ascii=False, indent=2)

            print(f"成功导出 {len(processed_data)} 条记录到 {output_file}")

    except Exception as e:
        print(f"导出失败：{str(e)}")
    finally:
        conn.close()


# 配置数据库信息
config = {
    "host": "192.168.112.17",
    "user": "vaas",
    "password": "Etas_vaas!",
    "db_name": "vaas",
    "table_name": "event",
    "output_file": "event_data.json"
}

# 执行导出
if __name__ == "__main__":
    mysql_to_json(**config)
