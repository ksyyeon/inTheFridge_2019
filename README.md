# 👨‍🍳 2019 인 더 냉장고
### 식재료 사물인식을 통한 레시피 추천 어플리케이션
2019.09 ~ 2019.12 <br/>
팀 뿌셔뿌셔 <br/><br/>
<a href="https://youtu.be/VSjCGh8mqQc">👀 시연영상 보러가기</a>
## About the Project
인더냉장고는 사진촬영을 통해 식재료를 인식하고, 인식된 재료를 활용하여 만들 수 있는 음식의 레시피를 추천한다.
## Envrioment
* Android Studio
* Java
* PHP + Apache
* MySql Workbench
## Prerequite
* Google Cloud Vision API
## Description
* **DataBase** <br/><br/>
<img src="https://user-images.githubusercontent.com/69140802/171242020-6bc85d21-0fda-4d90-bb38-50bdaa6a7035.png" height="400px"></img><br/>
<a href="https://www.data.go.kr/dataset/15000158/openapi.do">공공 데이터 포털</a>에서 제공하는 레시피 기본 정보, 재료 정보, 과정 정보가 담긴 엑셀 파일을 이용해 3개의 테이블 구성<br/><br/>
* **Application** <br/><br/>
<img src="https://user-images.githubusercontent.com/69140802/171252511-5237d6cf-24ce-4606-9db8-5fd8a2267917.png" height="400px"></img> &nbsp; <img src="https://user-images.githubusercontent.com/69140802/171252098-16ad5513-678c-4620-93ab-e562af558700.png" height="400px"></img> &nbsp; <img src="https://user-images.githubusercontent.com/69140802/171252390-aafc1820-e77f-4c8f-a4a4-cd7a8fbe7614.png" height="400px"></img><br/>
Cloud Vision API를 이용해 사진으로부터 재료명을 추출한다.</br></br>
<img src="https://user-images.githubusercontent.com/69140802/171254676-85e59429-fd82-4d1d-bb4a-2bad11ba82af.png" height="300px"></img> &nbsp; <img src="https://user-images.githubusercontent.com/69140802/171255556-a42cbb79-7d5c-4470-91e4-3d6e2aa91034.png" height="400px"></br>
인식된 식재료가 요리의 주재료로 쓰이는지, 부재료로 쓰이는지에 따라 요리에 점수를 부여한다. 재료를 활용한 요리의 목록을 점수순, 이름순, 조리시간순으로 보여준다. 요리를 선택하면 상세 조리과정을 제공한다.<br/><br/>
<img src="https://user-images.githubusercontent.com/69140802/171256541-c27bb932-ff66-4b2d-9290-5ea84d09f9b2.png" height="400px"></img> &nbsp; <img src="https://user-images.githubusercontent.com/69140802/171258356-a257fe85-bc05-470a-95f7-534b1f83e2d6.png" height="400px"></img> &nbsp; <img src="https://user-images.githubusercontent.com/69140802/171257351-9859066a-8a2f-4dfb-b46f-2d127fce9fb3.png" height="400px"></img><br/>
유저가 보유한 소스의 종류와 유통기한을 등록하면 재료 인식으로 레시피를 검색할 때 등록한 소스를 활용하는 레시피 중심으로 제공한다. 유통기한이 임박하면 푸시알림. 유통기한 등록방식은 달력, 유통기한 라벨 사진촬영 두가지다.</br></br>
## Files
