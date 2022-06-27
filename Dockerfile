FROM debian:buster-slim

RUN apt-get update
RUN apt-get -y install locales-all

ENV LANG ja_JP.UTF-8
ENV LANGUAGE ja_JP:ja
ENV LC_ALL ja_JP.UTF-8

RUN apt-get update && \
    apt-get install -y build-essential \
                       openjdk-11-jdk \
                       curl 
RUN apt-get install -y postgresql                       

CMD "/bin/bash"
