
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ContactForm{
	
	// csvの読み込み関数
	public static Vector<Vector<String>> readCsv(File f){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		
		try {
			
			FileInputStream s = new FileInputStream(f);
			InputStreamReader r = new InputStreamReader(s, "UTF-8");
			BufferedReader br = new BufferedReader(r);
			
			String line;
			
			while((line = br.readLine()) != null) {
				line = line.substring(0, line.length());
				String[] ary = line.split(",");
				Vector<String> v = new Vector<String>();
				for(String cell : ary) {
					v.add(cell);
				}
				data.add(v);
			}
			br.close();
			r.close();
			s.close();
			
		}catch(IOException e) {
			e.printStackTrace();
			
		}
		return data;
	}
	
	// csvの保存関数
	public static void saveToCSV(JTable table) {
		try {
			
			// ファイルを指定
			FileWriter csv = new FileWriter("csv/data.csv");
			
			// テーブルモデルを取得
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			// 行データを書き込み
			for(int i = 0; i < model.getRowCount(); i++) {
				for(int j = 0; j < model.getColumnCount(); j++) {
					csv.write(model.getValueAt(i, j).toString() + (j < model.getColumnCount() - 1 ? "," : ""));
				}
				csv.write("\n");
			}
			
			csv.close();
			System.out.println("CSVファイルに保存されました！");
			
		}catch(IOException e) {
			e.printStackTrace();
			System.out.println("ファイルの書き込みに失敗しました。");
		}
	}
	
	public static void main(String[] args) {
		
		/*
		 * テーブルの作成
		 */
				
		// csvの読み込み, 可変長のVector二次配列
		Vector<Vector<String>> client_data = readCsv(new File("csv/data.csv"));
		
		// JTableのヘッダーを作成, 可変長配列
		Vector<String> client_tags = new Vector<String>();
		client_tags.add("ID");
		client_tags.add("名前");
		client_tags.add("電話番号");
		client_tags.add("メールアドレス");
		
		// テーブルのオブジェクトを生成
		// デフォルトテーブルモデルをインスタンス化
		DefaultTableModel table_model = new DefaultTableModel(client_data, client_tags);
		// デフォルトテーブルモデルからJTableを作成
		JTable client_table = new JTable(table_model);
		
		//編集を無効化	
		client_table.setDefaultEditor(Object.class, null);

		/*
		 * 入力エリアを作成
		 */
		JTextField name_area = new JTextField(20);
		JTextField phone_area = new JTextField(20);
		JTextField mail_area = new JTextField(20);
		
		/*
		 * ラベルを作成
		 */
		Label id_label = new Label("ID");
		Label id_area = new Label("xxxx");
		Label name_label = new Label("名前");
		Label phone_label = new Label("電話番号");
		Label mail_label = new Label("メールアドレス");
		
		/*
		 * ボタンの作成		
		 */
		JButton new_button = new JButton("新規");
		JButton edit_button = new JButton("編集");		
		JButton delete_button = new JButton("削除");
		
		// 新規をクリックした時の処理
		new_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// テキストフィールドが空の時
				if(name_area.getText().isEmpty() || phone_area.getText().isEmpty() || mail_area.getText().isEmpty()) {
					// ポップアップを表示
					JOptionPane.showMessageDialog(null, "項目をすべて入力してください。");
				}else {
					// 行の追加
					table_model.addRow(args);

					// 現在の行数を取得
					int current_row = table_model.getRowCount() - 1; 
					
					// テキストフィールドの値を追加した行に代入
					client_table.setValueAt(current_row, current_row, 0); // id
					client_table.setValueAt(name_area.getText(), current_row, 1); // 名前
					client_table.setValueAt(phone_area.getText(), current_row, 2); // 電話
					client_table.setValueAt(mail_area.getText(), current_row, 3); // メール
					
					// テキストフィールドを初期値に戻す
					id_area.setText("xxxx");
					name_area.setText(null);
					phone_area.setText(null);
					mail_area.setText(null);
					
					// csvを保存
					saveToCSV(client_table);
				}
				
			}
		});

		// 削除をクリックした時の処理
		delete_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// アクティブな行がない場合、処理しない
				if(client_table.getSelectedRow() != -1) {
					// 選択された行の削除
					table_model.removeRow(client_table.getSelectedRow());
					
					// 現在の行数を取得
					int current_row = table_model.getRowCount(); 
					
					// idの振り直し
					for(int i = 0; i < current_row; i++) {
						client_table.setValueAt(i, i, 0);
					}
					
					// csvを保存
					saveToCSV(client_table);
				}else {
					// ポップアップを表示
					JOptionPane.showMessageDialog(null, "削除する行を選択してください。");
				}
			}
		});

		
		//編集をクリックした時の処理		
		edit_button.addActionListener(new ActionListener() {
			int count = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// 編集ボタン1回目の処理
				if(count == 0) {
					// アクティブな行がない場合、処理しない
					if(client_table.getSelectedRow() != -1) {
						// アクティブな行の値を取得
						Object active_id = client_table.getValueAt(client_table.getSelectedRow(), 0);
						Object active_name = client_table.getValueAt(client_table.getSelectedRow(), 1);
						Object active_phone = client_table.getValueAt(client_table.getSelectedRow(), 2);
						Object active_mail = client_table.getValueAt(client_table.getSelectedRow(), 3);
						
						// 入力欄に内容を反映
						id_area.setText((String)active_id);
						name_area.setText((String)active_name);
						phone_area.setText((String)active_phone);
						mail_area.setText((String)active_mail);
						
						// 編集以外のボタンを無効化する
						new_button.setEnabled(false);
						delete_button.setEnabled(false);
						count++;
					}else{
						// ポップアップを表示
						JOptionPane.showMessageDialog(null, "編集する行を選択してください。");
					};	

				// 編集ボタン再実行時
				}else {
					
					// テキストフィールドの値をアクティブなセルに代入
					client_table.setValueAt(id_area.getText(), client_table.getSelectedRow(), 0); // id
					client_table.setValueAt(name_area.getText(), client_table.getSelectedRow(), 1); // 名前
					client_table.setValueAt(phone_area.getText(), client_table.getSelectedRow(), 2); // 電話番号
					client_table.setValueAt(mail_area.getText(), client_table.getSelectedRow(), 3); // メール

					// テキストフィールドを初期値に戻す
					id_area.setText("xxxx");
					name_area.setText(null);
					phone_area.setText(null);
					mail_area.setText(null);
					
					// 編集以外のボタンを有効化する
					new_button.setEnabled(true);
					delete_button.setEnabled(true);
					count = 0;
					
					// csvを保存
					saveToCSV(client_table);
				}
			}
		});
				
		
		/*
		 *	ウィンドウの作成
		 */
		
		//ウィンドウのタイトルを設定
		JFrame frame = new JFrame("顧客管理ポートフォリオ");
		
		int frameWidth = 1280;
		int frameHeight = 720;
		
		//ウィンドウサイズを設定
		frame.setSize(frameWidth,frameHeight);
		
		// ウィンドウサイズを固定
		frame.setResizable(false);
		
		//作成したボタンを配置		
		frame.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 30, 10, 10);
		
		//テーブルを配置		
		JScrollPane scrollPane = new JScrollPane(client_table);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 10;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		frame.add(scrollPane, gbc);
		
		//gbcを初期化		
		gbc.gridheight = 1;
		gbc.gridwidth = 3;
		
		
		//入力欄を配置
		gbc.insets = new Insets(0, 0, 0, 0);
		//ID		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(id_label,gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 10, 30);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(id_area,gbc);
		
		//名前
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 0, 0, 30);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(name_label,gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.insets = new Insets(0, 0, 10, 30);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(name_area, gbc);
		
		//電話番号
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.insets = new Insets(0, 0, 0, 30);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(phone_label,gbc);
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.insets = new Insets(0, 0, 10, 30);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(phone_area,gbc);
		
		//メールアドレス	
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.insets = new Insets(0, 0, 0, 30);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(mail_label,gbc);
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.insets = new Insets(0, 0, 30, 30);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(mail_area,gbc);
		
		//ボタンの配置

		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 0.3;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(new_button,gbc);
		gbc.gridx = 2;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 0.3;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(edit_button,gbc);
		gbc.gridx = 3;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 0.3;
		gbc.insets = new Insets(0, 0, 0, 30);
		gbc.anchor = GridBagConstraints.WEST;
		frame.add(delete_button,gbc);
		
		//ウィンドウをモニターの中央に配置
		frame.setLocationRelativeTo(null);
		
		//ウィンドウを閉じたらプログラムを終了
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//ウィンドウを表示
		frame.setVisible(true);

		
	}

	
}
