FROM ubuntu:24.04
ENV DEBIAN_FRONTEND=noninteractive

# The default Ubuntu mirrors are slow from Korea, and large packages (e.g., the JDK) downloaded
# over plain HTTP can be cut off by some networks. Use the KAIST mirror over HTTPS and retry failed
# downloads. The KAIST mirror has both amd64 (/ubuntu) and arm64 (/ubuntu-ports, e.g., Apple Silicon)
# packages. ca-certificates is installed first (over HTTP) so that apt can use HTTPS.
RUN sed -i -e 's|http://archive.ubuntu.com/ubuntu|http://ftp.kaist.ac.kr/ubuntu|g' \
           -e 's|http://security.ubuntu.com/ubuntu|http://ftp.kaist.ac.kr/ubuntu|g' \
           -e 's|http://ports.ubuntu.com/ubuntu-ports|http://ftp.kaist.ac.kr/ubuntu-ports|g' \
           /etc/apt/sources.list.d/ubuntu.sources && \
    printf 'Acquire::Retries "5";\nAcquire::http::Timeout "30";\nAcquire::https::Timeout "30";\n' > /etc/apt/apt.conf.d/80-retries && \
    apt-get -y update && apt-get -y install ca-certificates && \
    sed -i -e 's|http://ftp.kaist.ac.kr/|https://ftp.kaist.ac.kr/|g' \
           /etc/apt/sources.list.d/ubuntu.sources

RUN apt-get -y update; apt-get -y upgrade
RUN apt-get -y install curl gnupg wget openjdk-11-jdk-headless


# From https://www.scala-sbt.org/download.html
# https://askubuntu.com/questions/1286545/what-commands-exactly-should-replace-the-deprecated-apt-key
# replacing 'deb https://' in the source lists by 'deb [signed-by=/etc/.../sbt-key.gpg] https://'
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" > key.gpg; \
    gpg --no-default-keyring --keyring ./temp-keyring.gpg --import key.gpg && \
    gpg --no-default-keyring --keyring ./temp-keyring.gpg --export --output sbt-key.gpg && \
    rm ./temp-keyring.gpg && \
    rm ./key.gpg && \
    mv sbt-key.gpg /etc/apt/keyrings/ && \
    sed -i -e 's/deb\shttps:\/\//deb \[signed-by\=\/etc\/apt\/keyrings\/sbt-key.gpg\] https:\/\//g' /etc/apt/sources.list.d/sbt*.list && \
    apt-get update && \
    apt-get install -y sbt

CMD export HOME=/home/sbt-user && sbt -mem 3072
