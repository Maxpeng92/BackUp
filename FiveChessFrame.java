package liky.game.frame;

import java.awt.Toolkit;
import java.awt.HeadlessException;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FiveChessFrame extends JFrame implements MouseListener, Runnable
{
	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	BufferedImage bgImage = null;
	int x = 0;
	int y = 0;
	int[][] allChess = new int[19][19];
	boolean isBlack = true;
	boolean canPlay = true;
	String message = "Black first!";
	int maxTime = 0;
	Thread t = new Thread(this);
	int blackTime = 0;
	int whiteTime = 0;
	String blackMessage = "Unlimited";
	String whiteMessage = "Unlimited";
	
	public FiveChessFrame()
	{
		this.setTitle("Five Chess Game");
		this.setSize(500, 500);
		this.setLocation((width-500)/2, (height-500)/2);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		t.start();
		t.suspend();
		this.repaint();
		String imagePath = "";
		try
		{
			imagePath = System.getProperty("user.dir")+"/bin/image/background.jpg";
			bgImage = ImageIO.read(new File(imagePath.replaceAll("\\\\", "/")));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g)
	{
		BufferedImage bi = new BufferedImage(500,500,BufferedImage.TYPE_INT_RGB);
		Graphics g2 = bi.createGraphics();
		g2.setColor(Color.black);
		g2.drawImage(bgImage, 1, 20, this);
		g2.setFont(new Font("Arial",Font.BOLD,20));
		g2.drawString("Game Information: "+message, 130, 60);
		g2.setFont(new Font("Calibri",0,14));
		g2.drawString("Black Time: "+blackMessage, 30, 470);
		g2.drawString("White Time: "+whiteMessage, 260, 470);
		for (int i=0;i<19;i++)
		{
			g2.drawLine(10, 70+20*i, 370, 70+20*i);
			g2.drawLine(10+20*i, 70, 10+20*i, 430);
		}
		g2.fillOval(68, 128, 4, 4);
		g2.fillOval(308, 128, 4, 4);
		g2.fillOval(308, 368, 4, 4);
		g2.fillOval(68, 368, 4, 4);
		g2.fillOval(308, 248, 4, 4);
		g2.fillOval(188, 128, 4, 4);
		g2.fillOval(68, 248, 4, 4);
		g2.fillOval(188, 368, 4, 4);
		g2.fillOval(188, 248, 4, 4);
		for (int i=0;i<19;i++)
		{
			for (int j=0;j<19;j++)
			{
				if (allChess[i][j] == 1)
				{
					int tempX = i*20 + 10;
					int tempY = j*20 + 70;
					g2.setColor(Color.black);
					g2.fillOval(tempX-7, tempY-7, 14, 14);
				}
				else if (allChess[i][j] == 2)
				{
					int tempX = i*20 + 10;
					int tempY = j*20 + 70;
					g2.setColor(Color.white);
					g2.fillOval(tempX-7, tempY-7, 14, 14);
					g2.setColor(Color.black);
					g2.drawOval(tempX-7, tempY-7, 14, 14);
				}
			}
		}
		g.drawImage(bi,0,0,this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void MousePressed(MouseEvent e)
	{
		if (canPlay == true)
		{
			x = e.getX();
			y = e.getY();
			if (x>=10&&x<=370&&y>=70&&y<=430)
			{
				x = (x-10)/20;
				y = (y-70)/20;
				if (allChess[x][y] == 0)
				{
					if (isBlack == true)
					{
						allChess[x][y] = 1;
						isBlack = false;
						message = "White turn";
					}
					else
					{
						allChess[x][y] = 2;
						isBlack = true;
						message = "Black turn";
					}
					boolean winFlag = this.checkWin();
					if (winFlag == true)
					{
						JOptionPane.showMessageDialog(this,"GAME OVER! "+(allChess[x][y]==1? "Black":"White")+" WINS!");
						canPlay = false;
					}
				}
				else 
				{
					JOptionPane.showMessageDialog(this, "Place your chess in other spots!");
				}
				this.repaint();
			}
		}
		if (e.getX()>=400&&e.getX()<=470&&e.getY()>=70&&e.getY()<=100)
		{
			int result = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?");
			if (result == 0)
			{
				for (int i=0;i<19;i++)
				{
					for (int j=0;j<19;j++)
					{
						allChess[i][j] = 0;
					}
				}
				isBlack = true;
				blackTime = maxTime;
				whiteTime = maxTime;
				if (maxTime > 0)
				{
					blackMessage = maxTime/3600+":"+(maxTime/60 - maxTime/3600*60)+":"+(maxTime-maxTime/60*60);
					whiteMessage = maxTime/3600+":"+(maxTime/60 - maxTime/3600*60)+":"+(maxTime-maxTime/60*60);
					t.resume();
				}
				else
				{
					blackMessage = "Unlimited";
					whiteMessage = "Unlimited";
				}
				this.canPlay = true;
				this.repaint();
			}
		}
		if (e.getX()>=400&&e.getX()<=470&&e.getY()>=120&&e.getY()<=150)
		{
			String inputs = JOptionPane.showInputDialog("Please type in the maximum time for the game (Unit: Min), if the enter '0', that means not time limit.");		
			try
			{
				maxTime = Integer.parseInt(inputs);
				if (maxTime < 0)
				{
					JOptionPane.showMessageDialog(this, "Please type correct information, negative number is not allowed.");
				}
				if (maxTime == 0)
				{
					int results = JOptionPane.showConfirmDialog(this, "Setup completed, do you want to restart the game?");
					if (results == 0)
					{
						for (int i=0;i<19;i++)
						{
							for (int j=0;j<19;j++)
							{
								allChess[i][j] = 0;
							}
						}
						isBlack = true;
						blackTime = maxTime;
						whiteTime = maxTime;
						blackMessage = "Unlimited";
						whiteMessage = "Unlimited";
						this.canPlay = true;
						this.repaint();
					}
				}
				if (maxTime>0)
				{
					int results = JOptionPane.showConfirmDialog(this, "Setup completed, do you want to restart the game?");
					if (results == 0)
					{
						for (int i=0;i<19;i++)
						{
							for (int j=0;j<19;j++)
							{
								allChess[i][j] = 0;
							}
						}
						isBlack = true;
						blackTime = maxTime;
						whiteTime = maxTime;
						blackMessage = maxTime/3600+":"+(maxTime/60 - maxTime/3600*60)+":"+(maxTime-maxTime/60*60);
						whiteMessage = maxTime/3600+":"+(maxTime/60 - maxTime/3600*60)+":"+(maxTime-maxTime/60*60);
						t.resume();
						this.canPlay = true;
						this.repaint();
					}
				}
			}
			catch(NumberFormatException e1)
			{
				JOptionPane.showMessageDialog(this, "Please enter correct information!");
			}
		}
		if (e.getX()>=400&&e.getX()<=470&&e.getY()>=170&&e.getY()<=200)
		{
			JOptionPane.showMessageDialog(this, "This is a five chess game, two players play around turn. Who has five chess in a line win the game.");
		}
		if (e.getX()>=400&&e.getX()<=470&&e.getY()>=270&&e.getY()<=300)
		{
			int results = JOptionPane.showConfirmDialog(this, "Do you want to give up?");
			if (results == 0)
			{
				if (isBlack)
				{
					JOptionPane.showMessageDialog(this, "White win! Game over.");
				}
				else 
				{
					JOptionPane.showMessageDialog(this, "Black win! Game over.");
				}
				this.canPlay = false;
			}
		}
		if (e.getX()>=400&&e.getX()<=470&&e.getY()>=320&&e.getY()<=350)
		{
			JOptionPane.showMessageDialog(this, "Author: Max Peng. @All right reserved.");
		}
		if (e.getX()>=400&&e.getX()<=470&&e.getY()>=370&&e.getY()<=400)
		{
			JOptionPane.showMessageDialog(this, "GAME OVER!");
			System.exit(0);
		}
	}

@Override
public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub
	
}

private boolean checkWin()
{
	boolean flag = false;
	int count = 1;
	int color = allChess[x][y];
	count = this.checkCount(1, 0, color);
	if (count>=5)
	{
		flag = true;
	}
	else
	{
		count = this.checkCount(0,1,color);
		if (count>=5)
		{
			flag = true;
		}
		else
		{
			count = this.checkCount(1, -1, color);
			if (count>=5)
			{
				flag = true;
			}
			else
			{
				count = this.checkCount(1, 1, color);
				if (count>=5)
				{
					flag = true;
				}
			}
		}
	}
	return flag;
}

private int checkCount(int xChange, int yChange, int color)
{
	int count = 1;
	int tempX = xChange;
	int tempY = yChange;
	while (x+xChange>=0&&x+xChange<=18&&y+yChange>=0&&y+yChange<=18&&color==allChess[x+xChange][y+yChange])
	{
		count++;
		if (xChange!=0)
			xChange++;
		if (yChange!=0)
		{
			if (yChange>0)
				yChange++;
			else
				yChange--;
		}
	}
	xChange = tempX;
	yChange = tempY;
	while (x-xChange>=0&&x-xChange<=18&&y-yChange>=0&&y-yChange<=18&&color==allChess[x-xChange][y-yChange])
	{
		count++;
		if (xChange!=0)
			xChange++;
		if (yChange!=0)
		{
			if (yChange>0)
				yChange++;
			else
				yChange--;
		}
	}
	return count;
}

public void run()
{
	if (maxTime>0)
	{
		while(true)
		{
			if(isBlack)
			{
				blackTime--;
				if (blackTime == 0)
				{
					JOptionPane.showMessageDialog(this, "Black is running out of time, game over.");
				}
			}
			else
			{
				whiteTime--;
				if (whiteTime == 0)
				{
					JOptionPane.showMessageDialog(this, "White is running out of time, game over.");
				}
			}
			blackMessage = maxTime/3600+":"+(maxTime/60 - maxTime/3600*60)+":"+(maxTime-maxTime/60*60);
			whiteMessage = maxTime/3600+":"+(maxTime/60 - maxTime/3600*60)+":"+(maxTime-maxTime/60*60);
			this.repaint();
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			System.out.println(blackTime+" -- "+whiteTime);
		}
	}
}
}
