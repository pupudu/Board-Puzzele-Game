
package board;

import java.awt.Color;

public class View extends javax.swing.JFrame {
    
    int boardsize;
    player comp;                        // Player object instance for computer Player
    player user;                        //Human Player
    private int[] board;                //integer array to represent the game board
    private int totalTokens=20;         // each player has 10 tokens
    boolean rolled=false;               //Concurrent controller for command buttons(roll dice)
    boolean ending=false;               // To identify the ending status
    boolean played=false;               //Concurrent controller for command buttons(Collect and Drop)
    public View() {                     //initiate GUI components via constructor
        initComponents();
    }
    
    void setupGame(String player,int boardSize){    //setup game
        this.boardsize=boardSize;
        user=new player(boardSize,player);          //instatiate human player
        comp=new player(boardSize,"Computer");      //instantiate computer player
        setBoard(boardsize);                        //call to setup boaard with given number of squares and insert random values to them
    }
   public void setBoard(int boardSize) {            //sets up the board
        board=new int[boardSize];                   //create a board represented by an array with given length or random value
        square=new javax.swing.JLabel[boardSize];   //array of Jlabels to represent the squares graphically
        jLabel15.setVisible(false);
            
        for(int i=0;i<boardSize;i++){               //insert random values to each square step by step through this loop
            double random=Math.random()*100;        //uses math randomizer to generate values between 1 and 100
            int rand=(int)Math.floor(random)+1;
            board[i]=rand;                          //assign random value to (i)th square
            
            square[i]=new javax.swing.JLabel();     //instatiate Jlabel
            square[i].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));   //apply a border to each label(for ease of view)
            square[i].setText(Integer.toString(rand));      //Text of JLabel represent the random value at corresponding square graphically
            jPanel1.add(square[i]);                 //add each label to the Jpanel which is set up in GRID LAYOUT
        }
    }
    int rollDice(){                                 //Dice rolled once
        double d=Math.random()*6;                   //random value generated as the dice value- double
        int n=(int) Math.floor(d)+1;                //convert to integer
        System.out.println("\nDice rolled:"+n);
        return n;
    }
    
    void playComp(){                                    //computer game strategy to win the game
        int half=40;                                
        int step=rollDice();                            //computer rolls dice to and assign its value to "step" variable
        comp.move(step);                                //computer moves steps on the board
        int position=comp.getPosition();                //gets the current position of the computer on the game board
        
        colorPlace(position, step, Color.green);        //mark its place on the board with green color
        
        int value=board[position];                      // scans the value on the current position
        
        if(comp.getPosition()>user.getPosition()){      //increase probability of dropping token
            half-=10;
        }
        
        int q=10-((comp.getPosition()/boardsize)*10);
        half-=q;
        
        System.out.println("Computer now at:"+position);
        if(position==boardsize-1){                      //game ends if landed on the last square
            System.out.println("Computer Landed at last square.Game Ends!");
            
            if(((totalTokens+value)%3==0) && ((comp.getTokens()+value)>user.getTokens()) ){               //collects value in last square only if the total becomes divisible by 3 and comp token cound greater than human(part of the winning strategy)
                comp.addTokens(value);                  //computer collects tokens
                System.out.println("Tokens collected");
                square[position].setForeground(Color.red);    //mark the square as empty
                square[position].setText("E");
            }
            else if(((totalTokens+value)%3==0) && ((comp.getTokens()+value)<user.getTokens())){
                comp.dropTokens(1);                     // divisible by 3, and comp token count less than human, then drop a token
                board[position]+=1;
                System.out.println("Comp dropped token");
            }
            else if((totalTokens+value)%3!=0 && comp.getTokens()>user.getTokens()){
                comp.dropTokens(1);                     //if not divisible by 3, drop a token
                board[position]+=1;
                System.out.println("Comp dropped token");
            }
            else{
                comp.addTokens(value);                  //computer collects tokens
                System.out.println("Tokens collected");
                square[position].setForeground(Color.red);    //mark the square as empty
                square[position].setText("E");
            }
            getGameResult();                            //generates the game result
            played=true;
            rolled=false;
            return;
            
        }
        if(value==0){                                   //if the current square is empty, computer has to give back half of his tokens
            int giveAway=comp.getTokens()/2;            //calculate the amount to give
            comp.dropTokens(giveAway);                  //computer drops tokens
            user.addTokens(giveAway);                   //user automatically collects them
            System.out.println("Empty Slot.You get:"+giveAway+" tokens from computer");
            square[position].setForeground(Color.MAGENTA);      //mark this special situation with a different color(Purple)
        }
        else if(position+6>=boardsize || user.getPosition()+6>=boardsize){  //strategy when one of the players are close to the end of the board 
           if(comp.getTokens()>=user.getTokens()){
               if((totalTokens+value+board[boardsize-1])%3==0){
                    comp.addTokens(value);                  //computer collects tokens
                    System.out.println("Tokens collected");
                    square[position].setForeground(Color.red);    //mark the square as empty
                    square[position].setText("E");
               }else{
                    comp.dropTokens(1);                     //if not divisible by 3, drop a token
                    board[position]+=1;
                    System.out.println("Comp dropped token");
               }
           }
           else{
               if((totalTokens+value+board[boardsize-1])%3==0){
                    comp.dropTokens(1);                     //if not divisible by 3, drop a token
                    board[position]+=1;
                    System.out.println("Comp dropped token");
               }else{
                    comp.addTokens(value);                  //computer collects tokens
                    System.out.println("Tokens collected");
                    square[position].setForeground(Color.red);    //mark the square as empty
                    square[position].setText("E");
               }
           }
        }
        else if(value>half){                              //collects tokens if the value of the current position is greater than 50(at least half of maximum possible value)
            comp.addTokens(value);                      //collect tokens
            board[position]=0;                          //mark the cell as empty
            System.out.println("Tokens collected");
            square[position].setForeground(Color.red);          //mark the cell as empty graphically by painting with red color and letter E
            square[position].setText("E");
        }
        else if(value<half){                              //drop token and move ahead if token value at current position is less than 50
            comp.dropTokens(1);                         //computer drops token
            board[position]+=1;                         //add increment token value at current position by 1
            System.out.println("Comp dropped token");
            
        }
        rolled=false;                                   //enables user input buttons
        update();                                       //updates the graphical table and shows the game progress so far(after each move)
    }
    @SuppressWarnings("unchecked")                      //code related to GUI
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton1.setText("Drop");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jButton2.setText("Roll Dice");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton3.setText("Collect");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel1.setText("Game Board");

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));

        jLabel2.setText("Position");

        jLabel3.setText("Moves");

        jLabel4.setText("Tokens");

        jLabel5.setBackground(new java.awt.Color(153, 153, 255));
        jLabel5.setText("Player");
        jLabel5.setOpaque(true);

        jLabel6.setBackground(new java.awt.Color(102, 255, 102));
        jLabel6.setText("Computer");
        jLabel6.setOpaque(true);

        jLabel7.setText("0");

        jLabel8.setText("0");

        jLabel9.setText("0");

        jLabel10.setText("0");

        jLabel11.setText("10");

        jLabel12.setText("10");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(64, 64, 64)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12)
                            .addComponent(jLabel10)
                            .addComponent(jLabel8)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(45, 45, 45))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/board/1game.jpg"))); // NOI18N
        jLabel15.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/board/2g.jpg"))); // NOI18N
        jLabel15.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(180, 180, 180)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 963, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    void colorPlace(int position,int prev,java.awt.Color c){        //method to change colors of the squares in graphical game board
        try{
            if(position-prev<0){
                square[position-prev+boardsize].setBackground(Color.GRAY);        //chnage color of previous square to gray
            }
            square[position-prev].setBackground(Color.gray);        //chnage color of previous square to gray
            square[position].setOpaque(true);                       //revert transparency
            square[position].setBackground(c);                      //set color of new position with given color
        }catch(Exception e){}
    }
    
    
    //this method refers to the button press action of the roll dice button
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(rolled==false){                                                       // can be pressed only if computer has finished its turn          
            int step=rollDice();                                                    //human player rolls dice
            user.move(step);                                                        //human player moves step on the board
            int position=user.getPosition();                                        //scans the current position
            int value=board[position];                                              //scans the token value at current position
            
            colorPlace(position, step,Color.BLUE);                                  //mark the current place of the human player with blue color
            
            if(position==boardsize-1){
                System.out.println("You Landed at last square.Game Ends!");         
                rolled=true;                                                        //disables roll dice button,
                played=false;                                                       //Enables collect and drop buttons, 
                ending=true;                                                        //Game ends after one more button click
            }
            if (value==0){                                                          //give away half of tokens owned 
                int giveAway=user.getTokens()/2;
                user.dropTokens(giveAway);
                comp.addTokens(giveAway);
                System.out.println("Empty Slot. You gave away:"+giveAway+" tokens to computer");
                square[position].setForeground(Color.MAGENTA);
                playComp();
            }
            else{
                System.out.println("Now You are at:"+Integer.toString(position));           
                System.out.println("Value at current Position:"+value);
                System.out.println("Enter 1 to collect value and 0 to skip");
                rolled=true;                                                        //disable roll dice button
                played=false;                                                       //enable collect and drop buttons
            }
            
        }
        update();                                                                   //update game progress table
    }//GEN-LAST:event_jButton2ActionPerformed
    void update(){                                                  //this method updates the game progress of the two players after each 
        jLabel7.setText(Integer.toString(user.getPosition()));      //Human player position
        jLabel9.setText(Integer.toString(user.getMoves()));         //Human player moves count
        jLabel11.setText(Integer.toString(user.getTokens()));       ////Human player tokens owned
        
        jLabel8.setText(Integer.toString(comp.getPosition()));      //computer Player Position on board
        jLabel10.setText(Integer.toString(comp.getMoves()));        //computer Player moves count
        jLabel12.setText(Integer.toString(comp.getTokens()));       //computer Player token count
        
    }
    
    //This method refers to the button click action of drop tokens Button
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(!played){                                
            int position=user.getPosition();            //get current position
            user.dropTokens(1);                         //drop one token from human player
            board[position]+=1;                         //add droped token to the board
            played=true;                                //disable this button to prevent clicking multiple times
            square[position].setText(Integer.toString(board[position]));    //display update on gui
            if(ending){                                 //this becomes final turn if ending is set to true
                getGameResult();
                played=true;
                rolled=true;
                return;
            }
            update();                                   //update game status on progress table
            playComp();                                 //Give turn back to the computer player
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed
    //this method refers to the button click action of the collect tokens button
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(!played){
            int position=user.getPosition();            //get current position
            int value=board[position];                  //get value at current position
            user.addTokens(value);                      //add value to players token collection
            board[position]=0;                          //empty the board slot
            played=true;                                //disable drop button and collect button
            square[position].setText("E");              //graphically mark empty slot
            square[position].setForeground(Color.red);          //graphically mark empty slot
            if(ending){
                getGameResult();                        //final move
                played=true;
                rolled=true;
                return;
            }
            playComp();                                 //give the turn to computer
            update();                                   //update game progress on the gui table
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    void announceWinner(int comp,int hum,String winner,String scene){       //this method announces 
        
        //oncommand line
        System.out.println(winner+" Wins\n...");
        System.out.println("Tokens owned by Computer:"+comp);
        System.out.println("Tokens Owned by Player:"+hum);
        System.out.println("Winning Basis:"+scene);
       
        
        //on gui
        if(winner.contains("Computer")){
            jLabel15.setEnabled(false);     //display image 1
        }
        else{
            jLabel5.setEnabled(true);       //display image 2
        }
        
        jLabel15.setVisible(true);
        
    }
  
    void rollAgain(){
        //Get result by rolling the dice once again
                int humanDiceVal=0;                 //first turn to human player
                int compDiceVal=0;                  //second turn to computer player
                while(humanDiceVal==compDiceVal){
                    humanDiceVal=rollDice();
                    compDiceVal=rollDice();
                }
                if(humanDiceVal>compDiceVal){       //human wins
                    announceWinner(comp.getTokens(), user.getTokens(), user.getName(), "Extra Dice Roll");
                }else{                              //computer wins
                    announceWinner(comp.getTokens(), user.getTokens(), "Computer", "Extra Dice Roll");
                }
    }
    void getGameResult(){
        update();                                       //update gui progress table for the final time
        totalTokens=comp.getTokens()+user.getTokens();  //calculate total token count
        if(totalTokens%3==0){                           //If total token count divisible by 3, winner will be the one with most tokens
            if(comp.getTokens()>user.getTokens()){                  //case 1- computer has most tokens
                announceWinner(comp.getTokens(), user.getTokens(), "Computer", "Highest Token Count");
            }else if(comp.getTokens()<user.getTokens()){            //case 2- human player has most tokens
                announceWinner(comp.getTokens(), user.getTokens(), user.getName(), "Highest Token Count");
            }
            else{                                                   //case 3- Both have same token count
                 rollAgain();           //extra roll winning basis
            }
        }
        else{
            if(comp.getMoves()<user.getMoves()){        //winning on lowest moves basis
                announceWinner(comp.getTokens(), user.getTokens(), "Computer", "Token Tight,minimal moves");
            }
            else if(comp.getMoves()>user.getMoves()){
                announceWinner(comp.getTokens(), user.getTokens(), user.getName(), "Token Tight,minimal moves");
            }
            else{
                rollAgain();    //extra roll winning basis
            }
        }
    }
        
        
    public static void main(String args[]) {
        
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>


        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                
                new View().setVisible(true);
               
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JLabel[] square;
}


class player{                   // Both as the human player and computer player
    private int tokens=10;                 //Number of tokens, initialy 10.
    private int position=0;               // position of the player on the board
    private int moves=0;                  //Number of moves played, initially nil.
    private int boardSize;
    private String name;
    
    public player(int boardSize, String name){       //initialize player according to board siz
        this.boardSize=boardSize;
        this.name=name;
    }
    
    void addTokens(int n){          // player adds n number of tokens to his token pile
        tokens+=n;
    }
    
    void dropTokens(int n){         // player drops n number of tokens from his token pile
        tokens-=n;
    }
    int getTokens(){                // returns the number of tokens owned by each player
        return tokens;
    }
    int getMoves(){                 // check the number of moves played by player
        return moves;
    }
    int getPosition(){              //check position on the board
        return position;
    }
    boolean move(int step){                 // player or comp plays his turn
        moves++;
        position+=step;                     //moves step on the board
        position=position%boardSize;        //move back to the begining if player reaches end of the board
        if (position==0){                   // send Game end signal if player reaches end of the board.
            return true;
        }
        return false;                       // send Game continue signal
    }
    String getName(){
        return name;
    }
}

