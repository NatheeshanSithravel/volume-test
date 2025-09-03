# Use a specific version for the base image
FROM amazoncorretto:21.0.6-alpine

# Create a non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set the working directory
WORKDIR /app

# Copy the application JAR file
COPY *.jar /app/app.jar

# Install tzdata and set the timezone in a single RUN statement
RUN apk --no-cache add tzdata && \
    cp /usr/share/zoneinfo/Asia/Colombo /etc/localtime && \
    echo "Asia/Colombo" > /etc/timezone && \
    apk del tzdata

#set log path previlages
RUN mkdir -p /logs
RUN chown -R appuser:appgroup /logs

# Set permissions for the application files
RUN chown -R appuser:appgroup /app

# Expose the application port
EXPOSE 8080

# Switch to the non-root user
USER appuser

# Command to run the application
CMD ["java", "-jar", "/app/app.jar"]
