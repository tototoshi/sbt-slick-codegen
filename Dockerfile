FROM centos:8

RUN rpm --import /etc/pki/rpm-gpg/RPM-GPG-KEY-centosofficial
RUN yum update -y && \
    yum install -y glibc-locale-source && \
    yum clean all

RUN localedef -f UTF-8 -i ja_JP ja_JP.UTF-8
RUN ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

ENV LANG=ja_JP.UTF-8
ENV TZ="Asia/Tokyo"

RUN yum install -y postgresql java-1.8.0-openjdk

CMD "/bin/bash"
