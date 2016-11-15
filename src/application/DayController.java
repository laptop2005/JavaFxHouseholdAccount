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
	
	//수정된 가계부 정보를 담을 맵
	private HashMap<String,HouseholdVO> updateMap;
	//삭제된 가계부 정보를 담을 맵
	private HashMap<String,HouseholdVO> deleteMap;
	//신규입력된 가계부정보를 담을 맵
	private HashMap<String,HouseholdVO> insertMap;
	//테이블 뷰에 바인드 될 데이터 리스트
	private ObservableList<HouseholdVO> tableDataList;
	//화면에 표시되는 테이블 뷰
	private TableView<HouseholdVO> tableView;
	//YYYYMMDD 기준일자
	private String date;
	//DB와 연동하기 위한 DAO
	private HouseholdDAO dao;
	
	//초기화 메소드
	//Initializable을 implements 하여 오버라이드 된 메스드
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//화면 불러올때 전역변수 객체들 초기화
		updateMap = new HashMap<>();
		deleteMap = new HashMap<>();
		insertMap = new HashMap<>();
		dao = new HouseholdDAO();
	}

	
	//달력에서 날짜 버튼을 클릭했을때 일출금 상세정보 팝업을 띄우는 이벤트 메소드
	@FXML
	@SuppressWarnings("unchecked")
	public void openPopup(ActionEvent event){
		try {
			//팝업창을 불러오기 위한 설정
			Stage stage = new Stage();
			
			//Input.fxml로 부터 컴포넌트를 불러와서 Scene에 배치한다.
			BorderPane pane = FXMLLoader.load(getClass().getResource("Input.fxml"));
			Scene scene = new Scene(pane, 400, 400);
			stage.setScene(scene);
			stage.initModality(Modality.WINDOW_MODAL);
			
			//팝업이 띄워진 부모창에서 연도와 월을 가져오기 위한 부분 
			Node targetNode = (Node)event.getTarget();
			Scene parentScene = targetNode.getScene();
			Label label_year = (Label)parentScene.lookup("#label_year");
			Label label_month = (Label)parentScene.lookup("#label_month");
			
			String year = label_year.getText();
			String month = label_month.getText();
			//일 은 클릭한 버튼 타이틀로 가져온다.
			String day = ((Button)targetNode).getText();
			
			//팝업창의 날짜 타이틀을 셋팅한다
			Label label_date = (Label)pane.lookup("#label_date");
			label_date.setText(year+"년 "+month+"월 "+day+"일");
			
			//테이블 뷰 설정시작
			tableView = (TableView)pane.lookup("#tableView_content");
			
			//가계부ID 컬럼 설정 - 사용자에게 보여줄 필요가 없는 컬럼이므로 visible은 false
			TableColumn householdId = new TableColumn<>();
			householdId.setVisible(false);
			householdId.setId("householdId");
			householdId.setCellValueFactory(new PropertyValueFactory<HouseholdVO,String>("householdId"));
			
			//가계부 날짜 컬럼 - 이것도 사용자에게 보여줄 필요가 없는 컬럼
			TableColumn householdDate = new TableColumn<>();
			householdDate.setVisible(false);
			householdDate.setId("householdDate");
			householdDate.setCellValueFactory(new PropertyValueFactory<HouseholdVO,String>("householdDate"));
			
			//가계부 내역 컬럼 - 테이블뷰에서 직접 내용을 수정할 수 있도록 TextFieldTableCell로 설정하고 
			//입력할때 신규추가냐 기존내용 수정이냐에 따라 처리를 다르게 할 수 있도록 이벤트를 설정한다.
			TableColumn householdHistory = new TableColumn<>("내역");
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
			
			//가계부 입금액 설정 - 위와 동일
			TableColumn householdInamt = new TableColumn<>("입금");
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
			
			//가계부 출금액 설정 - 위와 동일
			TableColumn householdOutamt = new TableColumn<>("출금");
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
			
			//일자를 YYYYMMDD 형식으로 만든다.
			if(month.length()==1){
				month = "0" + month;
			}
			if(day.length()==1){
				day = "0" + day;
			}
			date = year+month+day;
			
			//DB에서 불러온 가계부 리스트
			List<HouseholdVO> householdList = dao.selectHousehold(date);
			
			tableDataList = FXCollections.observableList(householdList);
			
			//테이블뷰에 표시할 데이터를 바인드한다.
			tableView.setItems(tableDataList);
			//테이블뷰에서 수정삭제가 가능해야 하므로 Editable은 true
			tableView.setEditable(true);
			//테이블뷰에 넣을 컬럼을 설정
			tableView.getColumns().addAll(householdId, householdDate, householdHistory,householdInamt,householdOutamt);
			
			//추가버튼을 위에서 찾은 pane에서 ID로 찾고 클릭했을때의 이벤트를 설정
			Button button_add = (Button)pane.lookup("#button_add");
			button_add.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					//vo를 신규로 생성해 디폴트 값을 넣고 테이블뷰에 바인드 된 리스트에 추가해 준다
					//vo를 new로 신규 생성하면 값이 null이 들어가 있는데 이 상태로 리스트에 넣게 되면 화면에 오류가 발생한다.
					HouseholdVO vo = new HouseholdVO();
					vo.setHouseholdId("");
					vo.setHouseholdDate(date);
					vo.setHouseholdHistory("");
					vo.setHouseholdInamt("0");
					vo.setHouseholdOutamt("0");
					//이미 테이블뷰에 바인드 되어있는 리스트이기 때문에 vo를 추가해 주는 것만으로 테이블뷰에도 데이터가 추가된다
					tableDataList.add(vo);
				}
			});
			
			//삭제버튼을 위에서 찾은 pane에서 ID로 찾고 클릭했을때의 이벤트를 설정
			Button button_delete = (Button)pane.lookup("#button_delete");
			button_delete.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					//테이블뷰에서 선택된 Row 위치를 가져온다
					int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
					//선택된 row위치로 데이터 리스트에서 VO를 찾는다
					HouseholdVO vo = tableDataList.get(selectedIndex);
					//householdId가 없으면 신규건 이므로 insertMap에서 삭제하고
					if(vo.getHouseholdId().isEmpty()){
						insertMap.remove(selectedIndex);
					}else{//householdId가 있으면 기존에 있던 건 이므로 deleteMap에 추가한다.
						deleteMap.put(vo.getHouseholdId(), vo);
					}
					//테이블뷰에 보여지고 있는 데이터를 삭제
					tableDataList.remove(selectedIndex);
				}
			});
			
			//저장버튼을 위에서 찾은 pane에서 ID로 찾고 클릭했을때의 이벤트를 설정
			Button button_save = (Button)pane.lookup("#button_save");
			button_save.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					//저장버튼을 클릭하면 updateMap에 넣어둔 가계부 데이터를 DB에 입력하고 updateMap을 비움
					Collection<HouseholdVO> updateCollection = updateMap.values();
					for(HouseholdVO vo:updateCollection){
						dao.updateHousehold(vo.getHouseholdId(), vo.getHouseholdDate(), vo.getHouseholdHistory(), vo.getHouseholdInamt(), vo.getHouseholdOutamt());
					}
					updateMap.clear();
					
					//저장버튼을 클릭하면 deleteMap에 넣어둔 가계부 데이터를 DB에 입력하고 deleteMap을 비움
					Collection<HouseholdVO> deleteCollection = deleteMap.values();
					for(HouseholdVO vo:deleteCollection){
						dao.deleteHousehold(vo.getHouseholdId());
					}
					deleteMap.clear();
					
					//저장버튼을 클릭하면 insertMap에 넣어둔 가계부 데이터를 DB에 입력하고 insertMap을 비움
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
