Create Table HOUSEHOLD 
(
  HOUSEHOLD_ID NUMBER NOT NULL,/*가계부 ID - 시퀀스로 값 할당 예정*/
  HOUSEHOLD_DATE VARCHAR2(8) NOT NULL,/*가계부 입력 일자*/
  HOUSEHOLD_HISTORY VARCHAR2(100),/*사용내역*/
  HOUSEHOLD_INAMT NUMBER(20),/*입금금액*/
  HOUSEHOLD_OUTAMT NUMBER(20),/*출금금액*/
  CONSTRAINT PK_HOUSEHOLD PRIMARY KEY (HOUSEHOLD_ID)/*프라이머리키를 ID로 설정하여 인덱스 생성*/
);

/*가계부 ID 값으로 사용할 시퀀스를 생성*/
create sequence SEQ_HOUSEHOLD_ID
start with 1
increment by 1
nomaxvalue 
minvalue 0; 