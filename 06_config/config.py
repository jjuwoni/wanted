import os 
from dotenv import load_dotenv

# .env 를 읽어올 수 있는 객체 갱성
load_dotenv()

class Settings:
    app_name = os.getenv("APP_NAME")
    api_key = os.getenv("API_KEY")
    model_name = os.getenv("MODEL_NAME")


settings = Settings()