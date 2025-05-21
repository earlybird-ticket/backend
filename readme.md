![image](https://github.com/user-attachments/assets/f8a62c93-a9f9-4f13-949d-b7ee765ed430)![image](https://github.com/user-attachments/assets/e7a36ad2-1f66-450c-a451-bd7119473faf)![image](https://github.com/user-attachments/assets/d283b7fb-4f0a-42c0-bbe4-d78ef1bd10ff)<div align="center">
  <h1>Earlybird-Ticket</h1>
</div>

![ServiceMainImage](https://github.com/user-attachments/assets/07e91e0a-02c2-457c-8290-3b5335fcf0a3)

# 📝 프로젝트 소개

- 저희 프로젝트는 1만명의 동시 예매 요청을 안정적으로 수용할 수 있는 티켓 예매 시스템 구축을 목표로 합니다.<br>
- 고객은 웹 사이트에서 VIP 선예매 와 일반 예매를 통해 티켓 예매를 할 수 있고, 쿠폰을 발급 받아 할인 된 가격으로 결제를 진행 할 수 있습니다.

# 🚩 프로젝트 목표

## 목표 트래픽

- 기존 서비스(인터파크) 참고하여 MAU 100만명으로 설정하였습니다.
    - 10,000석의 좌석 기준 5분 내 매진되며 오픈 직후 10초 이내의 전체 요청의 20% 몰린다고 가정했습니다.
- 트로트 콘서트 참고하여 경쟁률 10:1으로 예상했으며 인당 평균 20건의 재시도가 이루어진다고 가정했습니다.

| 항목         |     값      |
|------------|:----------:|
| 실제 예매 성공 수 |  10,000건   |
| 전체 요청 수    | 2,000,000건 |
| 평균 TPS     | 6,667 TPS  |
| 버스트 TPS    | 40,000 TPS |

## 티켓 예매 과정

- 티켓 예매 Sequence Flow

  ![SequenceDiagram](https://github.com/user-attachments/assets/7db55b00-d0ba-4b20-9912-f0323d7f3e73)

# 📌 주요기능

### 💳 VIP 선예매

> VIP 고객을 위한 `선예매 기능`을 제공합니다.
>
>
> 원하는 좌석 선점에 실패했을 경우, 해당 좌석에 `대기열에 등록`하여 순번을 기다릴 수 있습니다.
>
> `Redis`의 `Sorted Set`을 활용해 고성능 대기열 시스템을 구현했으며, 예약 실패나 취소가 발생하면 **`Kafka`를 통해 좌석 반환 이벤트를 처리**하고 다음 순번의 고객을 입장 시키는
> 방식의 서비스를 지원합니다.
>

### 🎟️ 일반 예매

> 일반 고객을 위한 `일반 예매 기능`을 제공합니다.
>
>
> 좌석 맵에서 원하는 좌석을 선택한 뒤, **선점 가능 여부를 확인하고**, 선점에 성공하면 **결제를 진행**할 수 있습니다.
>
> 수많은 요청이 동시에 몰리는 환경에서도 빠른 처리를 위해,
>
> **`Redis`를 사용해 좌석 선점 처리**를 수행하고, `Lua 스크립트`를 통해 `원자성`을 확보하여 안정적인 예매 서비스를 제공합니다.
>

### 💌 쿠폰

> 고객에게 **다양한 `쿠폰` 서비스**를 제공합니다.
>
>
> 특히, `생일 기념 할인 쿠폰`을 발급하여, 할인된 가격으로 결제할 수 있도록 지원하고 있습니다.
>
> `Spring Batch`를 적용해 생일 쿠폰을 **일괄 발급**하고,
>
> 기간이 지난 쿠폰은 **자동으로 회수**할 수 있도록 처리하고 있습니다.
>

### 💰 결제

> `토스페이(Toss Payments)`를 PG사로 연동하여 결제 기능을 제공합니다.
>
>
> 쿠폰을 적용해 **할인된 가격**으로 결제할 수 있으며, **선점한 좌석은 `10분 이내`에 결제를 완료해야 합니다.**
>
> 결제 시간 제한을 위해, **`Redis`의 `TTL`기능을 활용해 `예매 타임아웃`기능을 구현**하여,
>
> 시간 초과 시 좌석 선점이 취소되도록 처리하고 있습니다.
>

# 👨‍👩‍👧‍👦‍ 팀원 소개

<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/challduck">
      <img width=140px src="https://avatars.githubusercontent.com/u/123526228?v=4" alt=""/><br />
      <sub><b>최용석</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/Sunro1994">
      <img width=140px src="https://avatars.githubusercontent.com/u/132982907?v=4" alt=""/><br />
      <sub><b>이선로</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/ggumi030">
      <img width=140px src="https://avatars.githubusercontent.com/u/130031828?v=4" alt=""/><br />
      <sub><b>권수연</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/cnxw4570123">
      <img width=140px src="https://avatars.githubusercontent.com/u/96401835?v=4" alt=""/><br />
      <sub><b>정균민</b></sub></a><br /></td>
    </tr>

  </tbody>
</table>

# ⚙️ 프로젝트 아키텍처

<img src="https://private-user-images.githubusercontent.com/132982907/444393631-e8b9152c-df28-4f62-9d4f-892fa37408f1.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDc4MDQ1MzMsIm5iZiI6MTc0NzgwNDIzMywicGF0aCI6Ii8xMzI5ODI5MDcvNDQ0MzkzNjMxLWU4YjkxNTJjLWRmMjgtNGY2Mi05ZDRmLTg5MmZhMzc0MDhmMS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwNTIxJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDUyMVQwNTEwMzNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yMTA5YmY3NWJkZTA1MGVlNTIxOGFiMWYwNDNhY2I2ZjQ3ZGU4MGUzZWZkMWMwYTIwMjRhNmMyMDgwNTA3NjE4JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.Vt9egLkQyri65Z7-STEr--Sgpf9a6mOc-fkkTXPoWvU" />

# 📄 MSA 통신 아키텍처

<img width="517" alt="스크린샷 2025-05-16 오후 2 53 52" src="https://github.com/user-attachments/assets/416c1c28-6a60-48d4-8f40-83be23da474a" />

# 💻 기술 스택

<div align=center> 
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
<img src="https://img.shields.io/badge/postgreSQL-4169E1?style=for-the-badge&logo=postgreSQL&logoColor=white"> 
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white">
<img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white"> 
<img src="https://img.shields.io/badge/lua-2C2D72?style=for-the-badge&logo=lua&logoColor=white"> 
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white"> 
<img src="https://img.shields.io/badge/Promethues-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white"> 
<img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=Grafana&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/Zipkin-F48C00?style=for-the-badge&logo=zipkin&logoColor=white">
<img src="https://img.shields.io/badge/Apache Kafka-%3333333.svg?style=for-the-badge&logo=Apache%20Kafka&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/Apache Jmeter-D22128?style=for-the-badge&logo=Apache%20Jmeter&logoColor=white">
<img src="https://img.shields.io/badge/jpa-59666C?style=for-the-badge&logo=hibernate&logoColor=white"> 
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> 
<img src="https://img.shields.io/badge/Amazon ECS-FF9900?style=for-the-badge&logo=Amazon%20ECS&logoColor=white"> 
<img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white"> 
<img src="https://img.shields.io/badge/ElasticSearch-005571?style=for-the-badge&logo=ElasticSearch&logoColor=white"> 
<img src="https://img.shields.io/badge/Logstash-005571?style=for-the-badge&logo=Logstash&logoColor=white"> 
<img src="https://img.shields.io/badge/Kibana-005571?style=for-the-badge&logo=Kibana&logoColor=white"> 

</div>

# ⚒️ 성능 개선 사항

## Kafka 성능 개선

<table>
  <tbody>
    <tr>
      <td align="center">
      <img width=250px height="200" src="" alt=""/><br />
      <sub><b>단일 파티션, 단일 컨슈머 -> 500명 결과</b></sub><br /></td>
      <td align="center">
      <img width=250px height="200" src="https://private-user-images.githubusercontent.com/132982907/444406697-8fecc5c6-621a-4335-baa5-d9aaad9f9c2b.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDc4MDQ3MjIsIm5iZiI6MTc0NzgwNDQyMiwicGF0aCI6Ii8xMzI5ODI5MDcvNDQ0NDA2Njk3LThmZWNjNWM2LTYyMWEtNDMzNS1iYWE1LWQ5YWFhZDlmOWMyYi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwNTIxJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDUyMVQwNTEzNDJaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1iNmU2YTlmYThmYjg2OTBjYmE0OTZlZjhiNmQ4MDgwZDAzZTU2YTNjNDdiOWJjZTg2NDdjMDhmMWU0ODk4MGY4JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.QTOWeJpi0pWpxSwnqkuffFge7okiFU-qQembiGTX3GY" alt=""/><br />
      <sub><b>50 파티션, 50 컨슈머 -> 6500명 결과</b></sub><br /></td>
    </tr>
  </tbody>
</table>

## 티켓 예매 부하 테스트

### 1. Redisson 분산락 + DB Transaction

<table>
  <tbody>
    <tr>
      <td align="center">
      <img width=250px height="200" src="https://private-user-images.githubusercontent.com/96401835/445921227-d46d2d5f-c182-499e-9316-220fe3c86015.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDc4MjA2MzAsIm5iZiI6MTc0NzgyMDMzMCwicGF0aCI6Ii85NjQwMTgzNS80NDU5MjEyMjctZDQ2ZDJkNWYtYzE4Mi00OTllLTkzMTYtMjIwZmUzYzg2MDE1LnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA1MjElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNTIxVDA5Mzg1MFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTdlMGU2NjljNTUxYjQyZjBhZGE0ODlhODgwYTFlNWRjNzljY2Q3N2ViZWRkYzIxY2RhOTUxYmEwZjVmYWJiZDAmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.RsUXMn81J04HimMsj9yk8reoLJRrLiSwxEOJNrjbIXY" alt=""/><br />
      <sub><b>1000명</b></sub><br /></td>
      <td align="center">
      <img width=250px height="200" src="https://private-user-images.githubusercontent.com/96401835/445921275-fd7e4d43-4d69-462c-b29c-e4af64978d41.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDc4MjA2MzAsIm5iZiI6MTc0NzgyMDMzMCwicGF0aCI6Ii85NjQwMTgzNS80NDU5MjEyNzUtZmQ3ZTRkNDMtNGQ2OS00NjJjLWIyOWMtZTRhZjY0OTc4ZDQxLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA1MjElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNTIxVDA5Mzg1MFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTMxNDdhN2U2ZWI0ZGE0ZjM5Y2Y5ZjJkYmQ5MWM1M2U4MDc3NDRjOWY5NTEzYmEwMGI0MzMzOGRkYTdmZTE1MzAmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.kv7izb0JTczdx1PYCPvRuhDWSP7L1boUOL_vpQMVzAg" alt=""/><br />
      <sub><b>3000명</b></sub><br /></td>
      <td align="center">
      <img width=250px height="200" src="https://private-user-images.githubusercontent.com/96401835/445921275-fd7e4d43-4d69-462c-b29c-e4af64978d41.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDc4MjA2MzAsIm5iZiI6MTc0NzgyMDMzMCwicGF0aCI6Ii85NjQwMTgzNS80NDU5MjEyNzUtZmQ3ZTRkNDMtNGQ2OS00NjJjLWIyOWMtZTRhZjY0OTc4ZDQxLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA1MjElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNTIxVDA5Mzg1MFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTMxNDdhN2U2ZWI0ZGE0ZjM5Y2Y5ZjJkYmQ5MWM1M2U4MDc3NDRjOWY5NTEzYmEwMGI0MzMzOGRkYTdmZTE1MzAmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.kv7izb0JTczdx1PYCPvRuhDWSP7L1boUOL_vpQMVzAg" alt=""/><br />
      <sub><b>6500명</b></sub><br /></td>
    </tr>
  </tbody>
</table> 

### 2. Redis + Lua Script

<table>
  <tbody>
    <tr>
      <td align="center">
      <img width=250px height="200" src="https://private-user-images.githubusercontent.com/132982907/445925470-7e1105fe-d090-4f70-89bf-31898aa216b4.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDc4MjA2MzAsIm5iZiI6MTc0NzgyMDMzMCwicGF0aCI6Ii8xMzI5ODI5MDcvNDQ1OTI1NDcwLTdlMTEwNWZlLWQwOTAtNGY3MC04OWJmLTMxODk4YWEyMTZiNC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwNTIxJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDUyMVQwOTM4NTBaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0zY2ZmZjMzYmFjNzgwOTM5MzVmMjA2MjZjOTA0YmQ0NDFmYzAwMzMwOTZiYWNhNzg5ZGI0YjdmZTNjYTk0MmU0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.wbEGFpi3m69DEI3da4FDkx9vemIkJazgUkBWMf_6sGU" alt=""/><br />
      <sub><b>1000명</b></sub><br /></td>
      <td align="center">
      <img width=250px height="200" src="https://private-user-images.githubusercontent.com/96401835/445921338-51d27da8-1e0f-48bd-a84c-6d5ef4edac9f.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDc4MjA2MzAsIm5iZiI6MTc0NzgyMDMzMCwicGF0aCI6Ii85NjQwMTgzNS80NDU5MjEzMzgtNTFkMjdkYTgtMWUwZi00OGJkLWE4NGMtNmQ1ZWY0ZWRhYzlmLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA1MjElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNTIxVDA5Mzg1MFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTJlODAyZjlkY2EzMzY3MWQ3MjcxMGNjMzU5YzY3NWMzNjQ1NDY0ZDQyNjU5MDU1NDJlN2JmN2E4YTIzZjEwMGEmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.P4FBe9wOtXtAc3hg7DzisqMW_D7DgbUPZp4Yj5-M2u4" alt=""/><br />
      <sub><b>3000명</b></sub><br /></td>
      <td align="center">
      <img width=250px height="200" src="https://private-user-images.githubusercontent.com/96401835/445921366-f24ad034-292b-4c95-a393-5ce3f64b4861.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDc4MjA2MzAsIm5iZiI6MTc0NzgyMDMzMCwicGF0aCI6Ii85NjQwMTgzNS80NDU5MjEzNjYtZjI0YWQwMzQtMjkyYi00Yzk1LWEzOTMtNWNlM2Y2NGI0ODYxLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA1MjElMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNTIxVDA5Mzg1MFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTVkZjViYWM4ODVhMWQ1YTgxOGNmMzg0NzM1MTdiYjQwODc4OTBiM2FmNDI2N2E2MTVmNGY0NTkwNDNlMjdjMjAmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.eQk9kEMxZiQWK4NYPlGZu77X2ZrstjJ2AdNAXg7Rsfk" alt=""/><br />
      <sub><b>6500명</b></sub><br /></td>
    </tr>
  </tbody>
</table>

# 💣 트러블 슈팅
- [Lua Script를 사용하여 좌석 예매 원자성 보장 및 동시성 제어](https://github.com/earlybird-ticket/backend/wiki/%5B-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-%5D-Lua-Script%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-%EC%A2%8C%EC%84%9D-%EC%98%88%EB%A7%A4-%EC%9B%90%EC%9E%90%EC%84%B1-%EB%B3%B4%EC%9E%A5-%EB%B0%8F-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4)
- [Kafka & Outbox를 이용한 원자성 확보 트랜잭션 처리](https://github.com/earlybird-ticket/backend/wiki/%5B-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-%5D-Kafka-&-Outbox%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%9B%90%EC%9E%90%EC%84%B1-%ED%99%95%EB%B3%B4-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EC%B2%98%EB%A6%AC)
- [Producer & Consumer 간의 타입 일치 및 직렬화 역직렬화 문제 해결](https://github.com/earlybird-ticket/backend/wiki/%5B-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-%5D-Producer-&-Consumer-%EA%B0%84%EC%9D%98-%ED%83%80%EC%9E%85-%EC%9D%BC%EC%B9%98-%EB%B0%8F-%EC%A7%81%EB%A0%AC%ED%99%94-%EC%97%AD%EC%A7%81%EB%A0%AC%ED%99%94-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)
- [Redis Sentinel 기반 고가용성 및 데이터 유실 방지 설계](https://github.com/earlybird-ticket/backend/wiki/%5B-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-%5D-Redis-Sentinel-%EA%B8%B0%EB%B0%98-%EA%B3%A0%EA%B0%80%EC%9A%A9%EC%84%B1-%EB%B0%8F-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%9C%A0%EC%8B%A4-%EB%B0%A9%EC%A7%80-%EC%84%A4%EA%B3%84)
- [Redis를 이용한 좌석 맵 조회 및 예매 처리 속도 개선](https://github.com/earlybird-ticket/backend/wiki/%5B-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-%5D-Redis%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%A2%8C%EC%84%9D-%EB%A7%B5-%EC%A1%B0%ED%9A%8C-%EB%B0%8F-%EC%98%88%EB%A7%A4-%EC%B2%98%EB%A6%AC-%EC%86%8D%EB%8F%84-%EA%B0%9C%EC%84%A0)
- [Spring Batch를 이용한 Outbox 테이블 부하 방지](https://github.com/earlybird-ticket/backend/wiki/%5B-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-%5D-Spring-Batch%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-Outbox-%ED%85%8C%EC%9D%B4%EB%B8%94-%EB%B6%80%ED%95%98-%EB%B0%A9%EC%A7%80)
