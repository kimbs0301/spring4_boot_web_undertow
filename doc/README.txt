언더토우

Undertow는 Java로 작성된 유연하면서 고성능의 웹서버이며 NIO기반의 blogcking / Non-blocking API를 제공한다.
Undertow는 컴퍼지션 아키텍처를 제공하여 작은 단위의 용도를 갖는 핸들러들를 연결하여 웹서버를 구축 할 수 있다.
컴포지션 아키텍처는 Full Java EE Servlet 3.1 컨테이너에서 부터 Low Level의 Non-blocking 핸들러 또는 그 중간 수준의 기능을 선택을 할 수 있는 유연성을 제공해 준다.

Undertow는 쉽게 사용할 수 있는 높은 수준의 Builder API를 통해 완전히 임베딩 할 수 있도록 설계되어 있다.
Undertow의 라이프 사이클은 임베딩 애플리케이션에서 완벽하게 제어 할 수 있다.


이클립스 VM arguments
-server -Xms256m -Xmx256m -XX:+UseG1GC -XX:+UnlockDiagnosticVMOptions -XX:InitiatingHeapOccupancyPercent=35 -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=local

cd /workspace/luna/spring4_boot_web_undertow
git add -A
git commit -a -m "ok"
git push



for ((i=1;i<=100000;i++)); do curl -v "http://localhost:8080/mvc/test"; echo "
"; done


http://undertow.io/undertow-docs/undertow-docs-1.3.0/index.html
