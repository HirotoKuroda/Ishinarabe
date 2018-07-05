import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class Configure{
	int edge = 20;
	int r_margin = 20;
	int l_margin = 20;
	int t_margin = 20;
	int b_margin = 20;
	Configure(String filename){
		File file = new File(filename);
		if (!file.exists()) {
			System.out.println("設定ファイルが見つかりません");
			System.out.println("全てデフォルトの値を設定しました");
			return;
		}
		try{
			Scanner fileIn = new Scanner(file);
			while (fileIn.hasNextLine()) {
				String line = fileIn.nextLine();
				String [] str = line.split("[¥¥s]*,[¥¥s]*");
				int val = Integer.parseInt(str[1]);
				if (str.length!=2) {
					System.out.println("解析不能な行"+line);
					continue;
				}
				if (str[0].equalsIgnoreCase("edge")) {
					edge = val;
				}else if (str[0].equalsIgnoreCase("r_margin")) {
					r_margin = val;
				}else if (str[0].equalsIgnoreCase("l_margin")) {
					l_margin = val;
				}else if (str[0].equalsIgnoreCase("t_margin")) {
					t_margin = val;
				}else if (str[0].equalsIgnoreCase("b_margin")) {
					b_margin = val;
				}else{
					System.out.println("設定不能"+str[0]);
				}
			}
			fileIn.close();
		}catch(FileNotFoundException err){
			System.out.println(err);
		}
	}
	void print(){
		System.out.println("一マスの辺"+edge);
		System.out.println("右マージン"+r_margin);
		System.out.println("左マージン"+l_margin);
		System.out.println("上マージン"+t_margin);
		System.out.println("下マージン"+b_margin);
	}
}
class Board {
	int [][] state;
	int width;
	int height;
	int win;

	int num;//手数
	int current;//手番
	final int no_stone = 0;
	final int black = 1;
	final int white = 2;
	final int error = -1;


	boolean bStart;
	boolean bEnd;
	int winner;

	Board(int w ,int h ,int n){
		this.width = w;
		this.height = h;
		state = new int[w][h];
		this.win = n;
		num=0;
		current=black;//先手は黒
		winner = no_stone;
		bStart=true;
		bEnd=false;
	}
	void put(int x,int y,int stone){
		state[x][y]=stone;
		num++;
	}
	void turn(){
		if (current==black) {
			current=white;
		}else {
			current=black;
		}
	}
	int get(int i,int j){
		if (i<0||j<0||i>=width||j>=height) {
			return error;
		}
		return state[i][j];
	}
	void setWinner(int color){
		bEnd = true;
		winner =color;
	}
	boolean isStart(){
		return bStart;
	}
	boolean isEnd(){
		return bEnd;
	}
	boolean isFull(){
		if(num<width*height)
			return false;
		return true;
	}
	boolean check(int x,int y,int stone){
		int i=x,j=y;
		int hw=1;//並んだ数
		int hh=1;//並んだ数
		int hd1=1;//並んだ数
		int hd2=1;//並んだ数

		i=x+1;
		while(get(i,j)==stone){
			hw++;
			i++;
		}
		i=x-1;
		while(get(i,j)==stone){
			hw++;
			i--;
		}

		i=x;
		j=y+1;
		while(get(i,j)==stone){
			hh++;
			j++;
		}
		j=y-1;
		while(get(i,j)==stone){
			hh++;
			j--;
		}

		//斜め
		// 右肩下がり
		i=x-1;
		j=y-1;
		while(get(i,j)==stone){
			hd1++;
			i--;
			j--;
		}
		i=x+1;
		j=y+1;
		while(get(i,j)==stone){
			hd1++;
			i++;
			j++;
		}

		// 右肩上がり
		i=x+1;
		j=y-1;
		while(get(i,j)==stone){
			hd2++;
			i++;
			j--;
		}
		i=x-1;
		j=y+1;
		while(get(i,j)==stone){
			hd2++;
			i--;
			j++;
		}
		if (hw>=win||hh>=win||hd1>=win||hd2>=win) {
			return true;
		}
		return false;
	}
}
class SwingDraw extends JPanel{
	final int bstroke = 2;
	Configure cf ;
	Board bd;
	String title;
	MainFrame frame;
	
	int width,height,e,r_m,l_m,t_m,b_m;
	boolean current = true; //手番（白が先）
	SwingDraw(MainFrame f){
		frame = f;
		
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		setBackground(Color.white);

		Graphics2D g2 = (Graphics2D)g;
		BasicStroke w = new BasicStroke(bstroke);
		g2.setStroke(w);
		g2.setColor(Color.black);

		width = frame.width;
		height = frame.height;
		e=frame.cf.edge;
		r_m=frame.cf.r_margin;
		l_m=frame.cf.l_margin;
		t_m=frame.cf.t_margin;
		b_m=frame.cf.b_margin;

		for (int i = 0;i<= frame.bd.width; i++) {
			g2.drawLine(l_m+e*i,t_m,l_m+e*i,height-b_m);
		}
		for (int i = 0;i<= frame.bd.height; i++) {
			g2.drawLine(l_m,t_m+e*i,width-r_m,t_m+e*i);
		}
		//石をおく
		for (int i=0;i<frame.bd.width ;i++ ) {
			for (int j=0;j<frame.bd.height ;j++ ) {
				if (frame.bd.get(i,j)==2){
					g2.drawOval(l_m+e/10+e*i,t_m+e/10+e*j, e/5*4, e/5*4);
				}else if(frame.bd.get(i,j)==1){
					g2.fillOval(l_m+e/10+e*i,t_m+e/10+e*j, e/5*4, e/5*4);
				}
			}
		}
		if (frame.bd.isEnd()) {
			g2.setColor(Color.red);
			g2.drawString("End",frame.cf.l_margin,frame.cf.t_margin);
		}
	}
}
class MouseCheck implements MouseListener{
	MainFrame frame;
	MouseCheck(MainFrame f){
		frame=f;
	}
	public void mouseEntered(MouseEvent e){
		// repaint();
	}

	public void mouseExited(MouseEvent e){
		// repaint();
	}

	public void mousePressed(MouseEvent e){


		int x=e.getX(),y=e.getY(),b=e.getButton();
		if (b!=MouseEvent.BUTTON1) {
			return;
		}
		if (frame.bd.isEnd()) {
			return;
		}
		//マージンを引く
		x-=frame.cf.l_margin;
		y-=frame.cf.t_margin;
		if (x<0||y<0) {
			return;
		}
		if (x>=frame.cf.edge*frame.bd.width||y>=frame.cf.edge*frame.bd.height) {
			return;
		}
		//マス目に変換する
		int px =x/frame.cf.edge;
		int py =y/frame.cf.edge;

		System.out.print("横"+(px+1)+"マス目,");
		System.out.println("縦"+(py+1)+"マス目です");
		if (px<0||px>=frame.bd.width||py<0||py>=frame.bd.height) {
			return;
		}
		if (frame.bd.get(px,py)!=frame.bd.no_stone) {
			return;
		}
		frame.bd.put(px,py,frame.bd.current);
		if (frame.bd.check(px,py,frame.bd.current)) {
			if (frame.bd.current==frame.bd.black) {
				System.out.println("黒の勝ち");
			}else if (frame.bd.current==frame.bd.white) {
				System.out.println("白の勝ち");
			}
			frame.bd.setWinner(frame.bd.current);
		}else if(frame.bd.isFull()){
			frame.bd.setWinner(frame.bd.no_stone);
			System.out.println("引き分け");
		}
			frame.bd.turn();
		frame.panel.repaint();
		// repaint();
	}

	public void mouseReleased(MouseEvent e){
		// repaint();
	}

	public void mouseClicked(MouseEvent e){
	}
}
class MainFrame extends JFrame{
	Configure cf; //マスのサイズなど
	Board bd;//基盤の情報
	int width,height;
	SwingDraw panel;

	MainFrame(String title,Configure c,Board b){
		super(title);
		cf = c;
		bd = b;
		panel = new SwingDraw(this);
		Container cp = getContentPane();
		cp.add(panel);

		// マウスリスナー
		MouseCheck ms = new MouseCheck(this);
		panel.addMouseListener(ms);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//ウインドウの幅
		width = cf.edge*bd.width+cf.l_margin+cf.r_margin;
		//ウインドウの高さ
		height = cf.edge*bd.height+cf.t_margin+cf.b_margin;
		Dimension d = new Dimension(width,height);
		cp.setPreferredSize(d);
		pack();
		setVisible(true);
		//確認用出力
		System.out.println(width+"x"+height+"pixelのウインドウを作りました.");
	}
}
class ConnectN{
	public static void main(String[] args) {
		Scanner stdIn = new Scanner(System.in);
		System.out.println("ファイルの名前");
		String filename = stdIn.next();
		Configure cf = new Configure(filename);
		cf.print();
		int n,w,h;
		do{
			System.out.println("何マス並べる");
			n = stdIn.nextInt();
		}while(n<3);
		do{
			System.out.println("横は何マス");
			w = stdIn.nextInt();
		}while(w<n);
		do{
			System.out.println("縦は何マス");
			h = stdIn.nextInt();
		}while(h<n);
		// 碁盤の作成
		Board bd = new Board(w,h,n);
		MainFrame frame = new MainFrame("石並べ",cf,bd);
	}
}