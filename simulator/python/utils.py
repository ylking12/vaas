import os
import yaml
from loguru import logger


def getCfg():
    current_path = os.path.abspath(".")
    print(current_path)
    filename = os.path.join(current_path, "configuration.yml")
    with open(filename, 'r', encoding="utf-8") as f:
        data = f.read()
        yaml.warnings({'YAMLLoadWarning': False})
        yml = yaml.safe_load(data)
    logger.debug(yml)
    return yml
