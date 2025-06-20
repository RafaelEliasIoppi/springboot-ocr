FROM mcr.microsoft.com/devcontainers/java:17
RUN apt-get update \
  && apt-get install -y maven tesseract-ocr tesseract-ocr-por \
  && rm -rf /var/lib/apt/lists/*

# corrige o path para onde o por.traineddata foi instalado
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
