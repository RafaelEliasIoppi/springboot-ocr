FROM mcr.microsoft.com/devcontainers/java:11

RUN apt-get update \
  && apt-get install -y tesseract-ocr tesseract-ocr-por \
  && rm -rf /var/lib/apt/lists/*

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata
