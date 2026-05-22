FROM python:3.11-slim

WORKDIR /app

# Install system dependencies (all in one layer for efficiency)
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    fonts-liberation \
    fonts-unifont \
    libasound2 \
    libasound2-plugins \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcups2 \
    libdbus-1-3 \
    libdrm2 \
    libgbm1 \
    libglib2.0-0t64 \
    libgtk-3-0 \
    libnspr4 \
    libnss3 \
    libpango-1.0-0 \
    libpangocairo-1.0-0 \
    libx11-6 \
    libx11-xcb1 \
    libxcb-dri3-0 \
    libxcb1 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxi6 \
    libxkbcommon0 \
    libxrandr2 \
    libxrender1 \
    libxss1 \
    libxtst6 \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Install Playwright browsers (system deps already installed above)
RUN playwright install chromium

COPY app/ ./app/

RUN mkdir -p browser_data logs screenshots

EXPOSE 8001

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8001"]
