from algorithm_6axis import wit_data_process_service
from loguru import logger
from utils import getCfg

yml = getCfg()

cache_key = yml['redis_key_to_consume']

if __name__ == '__main__':
    logger.info("starting the process..")
    logger.add("./logs/data_process.log", rotation="1 day", retention="6 month", compression="zip", backtrace=False,
               diagnose=True)

    wit_data_process_service.wit_data_handler(cache_key)

