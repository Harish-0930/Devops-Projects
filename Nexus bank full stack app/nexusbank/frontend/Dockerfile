
# Build stage. react-scripts 5 is supported on the Node 20 LTS line.
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./

# Cache downloaded packages between Docker builds and avoid audit/funding calls
# that do not affect the production bundle.
RUN --mount=type=cache,target=/root/.npm npm ci --no-audit --no-fund

COPY . .

RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]