FROM node:18-bullseye-slim

WORKDIR /app

# Install dependencies required by node-canvas
RUN apt-get update && apt-get install -y \
    build-essential \
    libcairo2-dev \
    libpango1.0-dev \
    libjpeg-dev \
    libgif-dev \
    librsvg2-dev \
    && rm -rf /var/lib/apt/lists/*

COPY package.json ./

RUN npm install

COPY . .

# Download face-api.js models during build
RUN npm run download-models

EXPOSE 5001

CMD ["npm", "start"]
