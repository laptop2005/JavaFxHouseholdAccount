package application;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import dao.HouseholdDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vo.HouseholdVO;

public class DayController implements Initializable{
	
	//������ ����� ������ ���� ��
	private HashMap<String,HouseholdVO> updateMap;
	//������ ����� ������ ���� ��
	private HashMap<String,HouseholdVO> deleteMap;
	//�ű��Էµ� ����������� ���� ��
	private HashMap<String,HouseholdVO> insertMap;
	//���̺� �信 ���ε� �� ������ ����Ʈ
	private ObservableList<HouseholdVO> tableDataList;
	//ȭ�鿡 ǥ�õǴ� ���̺� ��
	private TableView<HouseholdVO> tableView;
	//YYYYMMDD ��������
	private String date;
	//DB�� �����ϱ� ���� DAO
	private HouseholdDAO dao;
	
	//�ʱ�ȭ �޼ҵ�
	//Initializable�� implements �Ͽ� �������̵� �� �޽���
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//ȭ�� �ҷ��ö� �������� ��ü�� �ʱ�ȭ
		updateMap = new HashMap<>();
		deleteMap = new HashMap<>();
		insertMap = new HashMap<>();
		dao = new HouseholdDAO();
	}

	
	//�޷¿��� ��¥ ��ư�� Ŭ�������� ����� ������ �˾��� ���� �̺�Ʈ �޼ҵ�
	@FXML
	@SuppressWarnings("unchecked")
	public void openPopup(ActionEvent event){
		try {
			//�˾�â�� �ҷ����� ���� ����
			Stage stage = new Stage();
			
			//Input.fxml�� ���� ������Ʈ�� �ҷ��ͼ� Scene�� ��ġ�Ѵ�.
			BorderPane pane = FXMLLoader.load(getClass().getResource("Input.fxml"));
			Scene scene = new Scene(pane, 400, 400);
			stage.setScene(scene);
			stage.initModality(Modality.WINDOW_MODAL);
			
			//�˾��� ����� �θ�â���� ������ ���� �������� ���� �κ� 
			Node targetNode = (Node)event.getTarget();
			Scene parentScene = targetNode.getScene();
			Label label_year = (Label)parentScene.lookup("#label_year");
			Label label_month = (Label)parentScene.lookup("#label_month");
			
			String year = label_year.getText();
			String month = label_month.getText();
			//�� �� Ŭ���� ��ư Ÿ��Ʋ�� �����´�.
			String day = ((Button)targetNode).getText();
			
			//�˾�â�� ��¥ Ÿ��Ʋ�� �����Ѵ�
			Label label_date = (Label)pane.lookup("#label_date");
			label_date.setText(year+"�� "+month+"�� "+day+"��");
			
			//���̺� �� ��������
			tableView = (TableView)pane.lookup("#tableView_content");
			
			//�����ID �÷� ���� - ����ڿ��� ������ �ʿ䰡 ���� �÷��̹Ƿ� visible�� false
			TableColumn householdId = new TableColumn<>();
			householdId.setVisible(false);
			householdId.setId("householdId");
			householdId.setCellValueFactory(new PropertyValueFactory<HouseholdVO,String>("householdId"));
			
			//����� ��¥ �÷� - �̰͵� ����ڿ��� ������ �ʿ䰡 ���� �÷�
			TableColumn householdDate = new TableColumn<>();
			householdDate.setVisible(false);
			householdDate.setId("householdDate");
			householdDate.setCellValueFactory(new PropertyValueFactory<HouseholdVO,String>("householdDate"));
			
			//����� ���� �÷� - ���̺�信�� ���� ������ ������ �� �ֵ��� TextFieldTableCell�� �����ϰ� 
			//�Է��Ҷ� �ű��߰��� �������� �����̳Ŀ� ���� ó���� �ٸ��� �� �� �ֵ��� �̺�Ʈ�� �����Ѵ�.
			TableColumn householdHistory = new TableColumn<>("����");
			householdHistory.setMinWidth(198);
			householdHistory.setEditable(true);
			householdHistory.setId("householdHistory");
			householdHistory.setCellValueFactory(new PropertyValueFactory<HouseholdVO,String>("householdHistory"));
			householdHistory.setCellFactory(TextFieldTableCell.forTableColumn());
			householdHistory.setOnEditCommit(new EventHandler<CellEditEvent>() {

				@Override
				public void handle(CellEditEvent event) {
					String newValue = event.getNewValue().toString();
					HouseholdVO vo = (HouseholdVO)event.getRowValue();
					vo.setHouseholdHistory(newValue);
					if(vo.getHouseholdId().isEmpty()){
						insertMap.put(event.getTablePosition().getRow()+"", vo);
					}else{
						updateMap.put(vo.getHouseholdId(), vo);
					}
				}
				
			});
			
			//����� �Աݾ� ���� - ���� ����
			TableColumn householdInamt = new TableColumn<>("�Ա�");
			householdInamt.setMinWidth(100);
			householdInamt.setEditable(true);
			householdInamt.setId("householdInamt");
			householdInamt.setCellValueFactory(new PropertyValueFactory<HouseholdVO,String>("householdInamt"));
			householdInamt.setCellFactory(TextFieldTableCell.forTableColumn());
			householdInamt.setOnEditCommit(new EventHandler<CellEditEvent>() {

				@Override
				public void handle(CellEditEvent event) {
					String newValue = event.getNewValue().toString();
					HouseholdVO vo = (HouseholdVO)event.getRowValue();
					vo.setHouseholdInamt(newValue);
					if(vo.getHouseholdId().isEmpty()){
						insertMap.put(event.getTablePosition().getRow()+"", vo);
					}else{
						updateMap.put(vo.getHouseholdId(), vo);
					}
				}
				
			});
			
			//����� ��ݾ� ���� - ���� ����
			TableColumn householdOutamt = new TableColumn<>("���");
			householdOutamt.setMinWidth(100);
			householdOutamt.setEditable(true);
			householdOutamt.setId("householdOutamt");
			householdOutamt.setCellValueFactory(new PropertyValueFactory<HouseholdVO,String>("householdOutamt"));
			householdOutamt.setCellFactory(TextFieldTableCell.forTableColumn());
			householdOutamt.setOnEditCommit(new EventHandler<CellEditEvent>() {

				@Override
				public void handle(CellEditEvent event) {
					String newValue = event.getNewValue().toString();
					HouseholdVO vo = (HouseholdVO)event.getRowValue();
					vo.setHouseholdOutamt(newValue);
					if(vo.getHouseholdId().isEmpty()){
						insertMap.put(event.getTablePosition().getRow()+"", vo);
					}else{
						updateMap.put(vo.getHouseholdId(), vo);
					}
				}
				
			});
			
			//���ڸ� YYYYMMDD �������� �����.
			if(month.length()==1){
				month = "0" + month;
			}
			if(day.length()==1){
				day = "0" + day;
			}
			date = year+month+day;
			
			//DB���� �ҷ��� ����� ����Ʈ
			List<HouseholdVO> householdList = dao.selectHousehold(date);
			
			tableDataList = FXCollections.observableList(householdList);
			
			//���̺�信 ǥ���� �����͸� ���ε��Ѵ�.
			tableView.setItems(tableDataList);
			//���̺�信�� ���������� �����ؾ� �ϹǷ� Editable�� true
			tableView.setEditable(true);
			//���̺�信 ���� �÷��� ����
			tableView.getColumns().addAll(householdId, householdDate, householdHistory,householdInamt,householdOutamt);
			
			//�߰���ư�� ������ ã�� pane���� ID�� ã�� Ŭ���������� �̺�Ʈ�� ����
			Button button_add = (Button)pane.lookup("#button_add");
			button_add.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					//vo�� �űԷ� ������ ����Ʈ ���� �ְ� ���̺�信 ���ε� �� ����Ʈ�� �߰��� �ش�
					//vo�� new�� �ű� �����ϸ� ���� null�� �� �ִµ� �� ���·� ����Ʈ�� �ְ� �Ǹ� ȭ�鿡 ������ �߻��Ѵ�.
					HouseholdVO vo = new HouseholdVO();
					vo.setHouseholdId("");
					vo.setHouseholdDate(date);
					vo.setHouseholdHistory("");
					vo.setHouseholdInamt("0");
					vo.setHouseholdOutamt("0");
					//�̹� ���̺�信 ���ε� �Ǿ��ִ� ����Ʈ�̱� ������ vo�� �߰��� �ִ� �͸����� ���̺�信�� �����Ͱ� �߰��ȴ�
					tableDataList.add(vo);
				}
			});
			
			//������ư�� ������ ã�� pane���� ID�� ã�� Ŭ���������� �̺�Ʈ�� ����
			Button button_delete = (Button)pane.lookup("#button_delete");
			button_delete.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					//���̺�信�� ���õ� Row ��ġ�� �����´�
					int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
					//���õ� row��ġ�� ������ ����Ʈ���� VO�� ã�´�
					HouseholdVO vo = tableDataList.get(selectedIndex);
					//householdId�� ������ �ű԰� �̹Ƿ� insertMap���� �����ϰ�
					if(vo.getHouseholdId().isEmpty()){
						insertMap.remove(selectedIndex);
					}else{//householdId�� ������ ������ �ִ� �� �̹Ƿ� deleteMap�� �߰��Ѵ�.
						deleteMap.put(vo.getHouseholdId(), vo);
					}
					//���̺�信 �������� �ִ� �����͸� ����
					tableDataList.remove(selectedIndex);
				}
			});
			
			//�����ư�� ������ ã�� pane���� ID�� ã�� Ŭ���������� �̺�Ʈ�� ����
			Button button_save = (Button)pane.lookup("#button_save");
			button_save.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					//�����ư�� Ŭ���ϸ� updateMap�� �־�� ����� �����͸� DB�� �Է��ϰ� updateMap�� ���
					Collection<HouseholdVO> updateCollection = updateMap.values();
					for(HouseholdVO vo:updateCollection){
						dao.updateHousehold(vo.getHouseholdId(), vo.getHouseholdDate(), vo.getHouseholdHistory(), vo.getHouseholdInamt(), vo.getHouseholdOutamt());
					}
					updateMap.clear();
					
					//�����ư�� Ŭ���ϸ� deleteMap�� �־�� ����� �����͸� DB�� �Է��ϰ� deleteMap�� ���
					Collection<HouseholdVO> deleteCollection = deleteMap.values();
					for(HouseholdVO vo:deleteCollection){
						dao.deleteHousehold(vo.getHouseholdId());
					}
					deleteMap.clear();
					
					//�����ư�� Ŭ���ϸ� insertMap�� �־�� ����� �����͸� DB�� �Է��ϰ� insertMap�� ���
					Collection<HouseholdVO> insertCollection = insertMap.values();
					for(HouseholdVO vo:insertCollection){
						dao.insertHousehold(vo.getHouseholdDate(), vo.getHouseholdHistory(), vo.getHouseholdInamt(), vo.getHouseholdOutamt());
					}
					insertMap.clear();
				}
			});
			
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
