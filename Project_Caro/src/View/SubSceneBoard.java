package View;

import java.util.Stack;

import Controller.ControllerGamePlayer;
import Controller.ControllerOfInitial;
import Model.Agent;
import Model.Board;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import project.caro.config.ConfigGame;


public class SubSceneBoard {
	int count = 0;
	Group group = new Group();
	Board board = null;
	Agent agent=null;
	SubScene subScene;
	private ControllerGamePlayer controller;
	private Stack<EventHandler<MouseEvent>> stackListenerMouseClick= new Stack<EventHandler<MouseEvent>>();
	public SubSceneBoard(Board board) {
		this.board=board;
		this.subScene=DrawBoard.createSubScene(board, getGroup());
		
	}
	public SubScene getSubScene() {
		return subScene;
	}
	public Group getGroup() {
		return group;
	}
	public boolean move(int rows, int cols,ConfigGame.Target target){
		return this.getBoard().move(rows, cols, target)!=null;
	}
	
	public void paintX(Group group,int row_index, int col_index) {
		Group x = new Group();
		Line line1 = new Line(0, 0, 50, 50);
		Line line2 = new Line(50, 0, 0, 50);
		line1.setStroke(Color.ORANGERED);
		line1.setStrokeWidth(5.0);
		line2.setStroke(Color.ORANGERED);
		line2.setStrokeWidth(5.0);
		x.getChildren().add(line1);
		x.getChildren().add(line2);
		x.setTranslateX(col_index*50);
		x.setTranslateY(row_index*50);
		x.setScaleX(0.5);
		x.setScaleY(0.5);
		group.getChildren().add(x);
	}
	public void paintO(Group group,int row_index, int col_index) {
		Group o = new Group();
		Circle circle= new Circle(50,null);
		circle.setStroke(Color.LAWNGREEN);
		circle.setStrokeWidth(5.0);
		o.getChildren().add(circle);
		o.setTranslateX(col_index*50+25);
		o.setTranslateY(row_index*50+25);
		o.setScaleX(0.4);
		o.setScaleY(0.4);
		group.getChildren().add(o);
	}
	
	public void addListenerMouseClickForOnePeople() {
		group.setPickOnBounds(true);
//		this.group.removeEventHandler(MouseEvent.MOUSE_CLICKED, this.listenerMouseClickForTwoPeople);
		EventHandler<MouseEvent> listenerMouseClickForOnePeople = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				double x = e.getSceneX() - ConfigGame.DRAW - SubSceneBoard.this.subScene.getLocalToSceneTransform().getTx();
				double y = e.getSceneY() - ConfigGame.DRAW - SubSceneBoard.this.subScene.getLocalToSceneTransform().getTy();
				int row_index = (int) (y / ConfigGame.DRAW);
				int col_index = (int) (x / ConfigGame.DRAW);
//				System.out.println(col_index + ":" +row_index);
				if(!SubSceneBoard.this.board.isValid(row_index, col_index)) {
					System.out.println("isValid");
					return;
				}
				SubSceneBoard.this.count++;
				if (SubSceneBoard.this.count % 2 == 1) {
					//X đi
					if (SubSceneBoard.this.board.matrix[row_index][col_index] != -1) {
						SubSceneBoard.this.count--;
					} else {
						Board boardTry = SubSceneBoard.this.board.move(row_index, col_index, ConfigGame.Target.X);
						if(boardTry!=null) {
							SubSceneBoard.this.board=boardTry;
							SubSceneBoard.this.paintX(group, row_index, col_index);
						}

					}

				} 
				SubSceneBoard.this.count++;
				controller.clock.setText(""+10);
				int[] location = agent.findBestMove(SubSceneBoard.this.board, ConfigGame.Target.O, ConfigGame.DEPTH);
				if(location!=null) {
					Board boardTry = SubSceneBoard.this.board.move(location[0], location[1], ConfigGame.Target.O);
					if(boardTry!=null) {
						SubSceneBoard.this.board=boardTry;
						SubSceneBoard.this.paintO(group, location[0], location[1]);
					}
				}
				
				
				ConfigGame.Status status = SubSceneBoard.this.board.getCurrentStatus(ConfigGame.Target.X);
				if(status!=ConfigGame.Status.NOT_OVER) {
					System.out.println(status);
					controller.stopClock();
					removeAllListenerMouseClick();
					
				}
				controller.clock.setText(""+10);
				
			}
		};
		this.group.addEventHandler(MouseEvent.MOUSE_CLICKED, listenerMouseClickForOnePeople);
		stackListenerMouseClick.push(listenerMouseClickForOnePeople);
	}
	public void addListenerMouseClickForTwoPeople() {
		group.setPickOnBounds(true);
		//this.subScene.z
		EventHandler<MouseEvent> listenerMouseClickForTwoPeople=e -> {
			
			double x = e.getSceneX() - ConfigGame.DRAW - SubSceneBoard.this.subScene.getLocalToSceneTransform().getTx();
			double y = e.getSceneY() - ConfigGame.DRAW - SubSceneBoard.this.subScene.getLocalToSceneTransform().getTy();
			int row_index = (int) (y / ConfigGame.DRAW);
			int col_index = (int) (x / ConfigGame.DRAW);
//			System.out.println(col_index + ":" +row_index);
			if(!SubSceneBoard.this.board.isValid(row_index, col_index)) {
				System.out.println("isValid");
				return;
			}
			count++;
			if (count % 2 == 1) {
				//X đi
				if (board.matrix[row_index][col_index] != -1) {
					count--;
				} else {
					Board boardTry = this.board.move(row_index, col_index, ConfigGame.Target.X);
					if(boardTry!=null) {
						this.board=boardTry;
						this.paintX(group, row_index, col_index);
						System.out.println("Tới O đi");
						controller.clock.setText(""+10);
						
					}

				}

			} else {
				//O đi
				if (board.matrix[row_index][col_index] != -1) {
					count--;
				} else {
					Board boardTry = this.board.move(row_index, col_index, ConfigGame.Target.O);
					if(boardTry!=null) {
						this.board=boardTry;
						this.paintO(group, row_index, col_index);
						System.out.println("Tới X đi");
						controller.clock.setText(""+10);
					}
				}
			}
			int maxNumX=this.board.check(ConfigGame.Target.X.VALUE);
			int maxNumO=this.board.check(ConfigGame.Target.O.VALUE);
			if ( maxNumX!= -1) {
				controller.stopClock();
				removeAllListenerMouseClick();
				System.out.println("X win");
			} else if (maxNumO != -1) {
				controller.stopClock();
				removeAllListenerMouseClick();
				System.out.println("O win");
			}
		};
		group.addEventHandler(MouseEvent.MOUSE_CLICKED, listenerMouseClickForTwoPeople);
		stackListenerMouseClick.push(listenerMouseClickForTwoPeople);
	}
	public void removeAllListenerMouseClick() {
		while(stackListenerMouseClick.size()!=0) {
			this.group.removeEventHandler(MouseEvent.MOUSE_CLICKED, stackListenerMouseClick.pop());
		}
	}
	public Board getBoard() {
		return board;
	}
	
	public void setAgent(Agent agent) {
		this.agent=agent;
		
		
	}
	public void setController(ControllerGamePlayer controller) {
		this.controller = controller;
		
	}
	public ConfigGame.Target getTurn() {
		if(count%2==1) {
			return ConfigGame.Target.O;
		}
		return ConfigGame.Target.X;
		
	}

}
