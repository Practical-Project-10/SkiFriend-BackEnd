#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ubuntu/app/step
IMAGEPATH=/home/ubuntu/app

echo "> 이미지 저장 폴더 만들기"
sudo mkdir ~/app/imageCollection

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/zip/skiFriend-0.0.1-SNAPSHOT.jar $REPOSITORY/"

sudo cp $REPOSITORY/zip/skiFriend-0.0.1-SNAPSHOT.jar $REPOSITORY/

sudo rm -rf $REPOSITORY/nohup.out

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/skiFriend-0.0.1-SNAPSHOT.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

sudo chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."

nohup java -jar \
    -Dspring.config.location=classpath:/application.properties,classpath:/application-$IDLE_PROFILE.properties \
    -Dspring.profiles.active=$IDLE_PROFILE \
    -Dimage.path=$IMAGEPATH \
    $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

#    ,/home/ubuntu/app/application-oauth.properties,/home/ubuntu/app/application-real-db.properties