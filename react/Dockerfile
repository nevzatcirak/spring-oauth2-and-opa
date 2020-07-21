FROM nginx:1.15.2-alpine
COPY ./build /var/www
RUN chmod 0755 /var/www/**/*
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
USER root
ENTRYPOINT ["nginx","-g","daemon off;"]