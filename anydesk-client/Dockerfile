FROM node:18 AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build -- --mode docker

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
