FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY services ./services
ENV PYTHONUNBUFFERED=1
RUN useradd --create-home app && mkdir /data && chown app /data
USER app
CMD ["python", "-m", "uvicorn", "services.ia.main:app", "--host", "0.0.0.0", "--port", "8001"]

