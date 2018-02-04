FROM openjdk

# COPY ALL FILES to the Container
COPY . .

RUN ./gradlew build


