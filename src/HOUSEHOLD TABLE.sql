Create Table HOUSEHOLD 
(
  HOUSEHOLD_ID NUMBER NOT NULL,/*����� ID - �������� �� �Ҵ� ����*/
  HOUSEHOLD_DATE VARCHAR2(8) NOT NULL,/*����� �Է� ����*/
  HOUSEHOLD_HISTORY VARCHAR2(100),/*��볻��*/
  HOUSEHOLD_INAMT NUMBER(20),/*�Աݱݾ�*/
  HOUSEHOLD_OUTAMT NUMBER(20),/*��ݱݾ�*/
  CONSTRAINT PK_HOUSEHOLD PRIMARY KEY (HOUSEHOLD_ID)/*�����̸Ӹ�Ű�� ID�� �����Ͽ� �ε��� ����*/
);

/*����� ID ������ ����� �������� ����*/
create sequence SEQ_HOUSEHOLD_ID
start with 1
increment by 1
nomaxvalue 
minvalue 0; 