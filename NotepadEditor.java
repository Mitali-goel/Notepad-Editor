import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.*;
import java.util.HashMap;
import javax.swing.text.*;
class NotepadEditor{
    JFrame frame;
    JTabbedPane tabs;
    int count =1;
    HashMap<JTextPane, File> fileMap = new HashMap<>();
    JMenuItem saveItem;
    SimpleAttributeSet currentStyle = new SimpleAttributeSet();
    NotepadEditor(){
     frame = new JFrame("Notepad");
     tabs = new JTabbedPane();
     createNewTab();
     JMenuBar menuBar = new JMenuBar();
     JMenu fileMenu = new JMenu("File");
     JMenu editMenu = new JMenu("Edit");
     JMenuItem newItemW = new JMenuItem("New Window");
     JMenuItem newItemT = new JMenuItem("New Tab");
     JMenuItem openItem = new JMenuItem("Open");
     saveItem = new JMenuItem("Save");
     JMenuItem saveAsItem = new JMenuItem("Save As");
     JMenuItem exitItem = new JMenuItem("Exit");
     JMenuItem cutItem = new JMenuItem("Cut");
     JMenuItem copyItem = new JMenuItem("Copy");
     JMenuItem pasteItem = new JMenuItem("Paste");
     JMenuItem selectAllItem = new JMenuItem("Select All");
     JMenuItem boldItem = new JMenuItem("Bold");
     JMenuItem italicItem = new JMenuItem("Italic");
     JMenuItem underlineItem = new JMenuItem("Underline");
     JMenuItem increaseFont = new JMenuItem("Increase Font");
     JMenuItem decreaseFont = new JMenuItem("Decrease Font");
     fileMenu.add(newItemW);
     fileMenu.add(newItemT);
     fileMenu.add(openItem);
     fileMenu.add(saveItem);
     fileMenu.add(saveAsItem);
     fileMenu.add(exitItem);
     editMenu.add(cutItem);
     editMenu.add(copyItem);
     editMenu.add(pasteItem);
     editMenu.add(selectAllItem);
     editMenu.add(boldItem);
     editMenu.add(italicItem);
     editMenu.add(underlineItem);
     editMenu.add(increaseFont);
     editMenu.add(decreaseFont);
     menuBar.add(fileMenu);
     menuBar.add(editMenu);
     saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK));
     saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK|InputEvent.SHIFT_DOWN_MASK));
     openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_DOWN_MASK));
     newItemT.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK));
     newItemW.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK));
     exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_DOWN_MASK));
     boldItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,InputEvent.CTRL_DOWN_MASK));
     italicItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,InputEvent.CTRL_DOWN_MASK));
     underlineItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,InputEvent.CTRL_DOWN_MASK));
     increaseFont.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
     decreaseFont.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
     fileMenu.addSeparator();
     editMenu.addSeparator();
     frame.setJMenuBar(menuBar);
     frame.add(tabs);
     frame.setSize(600,400);
     frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
     frame.addWindowListener(new WindowAdapter(){
        public void windowClosing(WindowEvent e){
            JTextPane textArea=getCurrentTextArea();
            if(confirmSave(textArea)){
                frame.dispose();
            }
        }
    });
     frame.setVisible(true);
     newItemW.addActionListener(e->{new NotepadEditor();});
     newItemT.addActionListener(e->{createNewTab();});
     exitItem.addActionListener(e->{
        JTextPane textArea=getCurrentTextArea();
        if(confirmSave(textArea)){
            frame.dispose();
        }
    });
     saveItem.addActionListener(e->{
        JTextPane textArea=getCurrentTextArea();
        File file=fileMap.get(textArea);
        if(file!=null && !file.exists()){
            int choice=JOptionPane.showConfirmDialog(frame,"File was deleted. Create new file?","Warning",JOptionPane.YES_NO_OPTION);
            if(choice==JOptionPane.NO_OPTION){
                return;
            }else{
                JOptionPane.showMessageDialog(frame,"File will be recreated automatically by save");
            }
        }
        if(file!=null){
            try{
                FileWriter writer=new FileWriter(file);
                writer.write(textArea.getText());
                writer.close();
            }catch(Exception ex){ex.printStackTrace();}
        }else{
            JFileChooser fileChooser=new JFileChooser();
            FileNameExtensionFilter textFilter=new FileNameExtensionFilter("Text Files(*.txt)","txt");
            fileChooser.addChoosableFileFilter(textFilter);
            fileChooser.setFileFilter(textFilter);
            fileChooser.setAcceptAllFileFilterUsed(true);
            int option=fileChooser.showSaveDialog(frame);
            if(option==JFileChooser.APPROVE_OPTION){
                try{
                    file=fileChooser.getSelectedFile();
                    if(fileChooser.getFileFilter() instanceof FileNameExtensionFilter){
                        FileNameExtensionFilter filter=(FileNameExtensionFilter)fileChooser.getFileFilter();
                        if(filter.getExtensions()[0].equals("txt")){
                            if(!file.getName().toLowerCase().endsWith(".txt")){
                                file=new File(file.getAbsolutePath()+".txt");
                            }
                        }
                    }
                    FileWriter writer=new FileWriter(file);
                    writer.write(textArea.getText());
                    writer.close();
                    fileMap.put(textArea,file);
                    setTabTitle(file.getName());
                    textArea.putClientProperty("dirty", false);
                }catch(Exception ex){ex.printStackTrace();}
            }
        }
    });
    saveAsItem.addActionListener(e->{
        JTextPane textArea=getCurrentTextArea();
        JFileChooser fileChooser=new JFileChooser();
        FileNameExtensionFilter textFilter=new FileNameExtensionFilter("Text Files(*.txt)","txt");
        fileChooser.addChoosableFileFilter(textFilter);
        fileChooser.setFileFilter(textFilter);
        fileChooser.setAcceptAllFileFilterUsed(true);
        int option=fileChooser.showSaveDialog(frame);
        if(option==JFileChooser.APPROVE_OPTION){
            try{
                File file=fileChooser.getSelectedFile();
                if(fileChooser.getFileFilter() instanceof FileNameExtensionFilter){
                    FileNameExtensionFilter filter=(FileNameExtensionFilter)fileChooser.getFileFilter();
                    if(filter.getExtensions()[0].equals("txt")){
                        if(!file.getName().toLowerCase().endsWith(".txt")){
                            file=new File(file.getAbsolutePath()+".txt");
                        }
                    }
                }
                FileWriter writer=new FileWriter(file);
                writer.write(textArea.getText());
                writer.close();
                fileMap.put(textArea,file);
                setTabTitle(file.getName());
                textArea.putClientProperty("dirty", false);
            }catch(Exception ex){ex.printStackTrace();}
        }
    });
     openItem.addActionListener(e->{
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Text Files(*.txt)","txt");
        fileChooser.addChoosableFileFilter(textFilter);
        fileChooser.setFileFilter(textFilter);
        fileChooser.setAcceptAllFileFilterUsed(true); 
        int option = fileChooser.showOpenDialog(frame);
        if(option == JFileChooser.APPROVE_OPTION){
            try{
            File file = fileChooser.getSelectedFile();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            JTextPane textArea = getCurrentTextArea();
            StringBuilder content = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                content.append(line).append("\n");
            }
            textArea.setText(content.toString());
            reader.close();
            textArea.putClientProperty("dirty", false);
            fileMap.put(textArea, file);
            setTabTitle(file.getName());
            }catch(Exception ex){ex.printStackTrace();}
        }
     });
     cutItem.addActionListener(e->{getCurrentTextArea().cut();});
     copyItem.addActionListener(e->{getCurrentTextArea().copy();});
     pasteItem.addActionListener(e->{getCurrentTextArea().paste();});
     selectAllItem.addActionListener(e->{getCurrentTextArea().selectAll();});
     boldItem.addActionListener(e -> applyStyle(1));
     italicItem.addActionListener(e -> applyStyle(2));
     underlineItem.addActionListener(e -> applyStyle(3));
     increaseFont.addActionListener(e -> applyStyle(4));
     decreaseFont.addActionListener(e -> applyStyle(5));
    }
    void createNewTab() {
        JTextPane textArea = new JTextPane();
        textArea.setContentType("text/plain");
        fileMap.put(textArea, null);
        textArea.putClientProperty("dirty", false);
        textArea.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){textArea.putClientProperty("dirty", true);}
            public void removeUpdate(DocumentEvent e){textArea.putClientProperty("dirty", true);}
            public void changedUpdate(DocumentEvent e){textArea.putClientProperty("dirty", true);}
        });
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(new LineNumberView(textArea));
        tabs.addTab("Untitled " + count, scrollPane);
        int index = tabs.getTabCount()-1;
        JPanel tabPanel= new JPanel();
        tabPanel.setOpaque(false);
        JLabel title = new JLabel("Untitled " + count+" ");
        JButton closeBtn = new JButton("x");
        closeBtn.setBorder(null);
        closeBtn.setFocusable(false);
        closeBtn.addActionListener(e->{
            int i=tabs.indexOfTabComponent(tabPanel);
            if(i!=-1){
                JScrollPane sp=(JScrollPane)tabs.getComponentAt(i);
                JViewport vp=sp.getViewport();
                JTextPane ta=(JTextPane)vp.getView();
                if(confirmSave(ta)){
                    tabs.remove(i);
                    if(tabs.getTabCount()==0){frame.dispose();}
                }
            }
        });
        tabPanel.add(title);
        tabPanel.add(closeBtn);
        tabs.setTabComponentAt(index,tabPanel);
        count++;
    }
    JTextPane getCurrentTextArea(){
        int index = tabs.getSelectedIndex();
        JScrollPane scrollPane = (JScrollPane)tabs.getComponentAt(index);
        JViewport viewport = scrollPane.getViewport();
        return (JTextPane) viewport.getView();
    }
    void setTabTitle(String name){
        int index = tabs.getSelectedIndex();
        JPanel tabPanel = (JPanel) tabs.getTabComponentAt(index);
        JLabel label =(JLabel) tabPanel.getComponent(0);
        label.setText(name + " ");
    }
    boolean confirmSave(JTextPane textArea){
        Boolean dirty=(Boolean)textArea.getClientProperty("dirty");
        if(dirty!=null && dirty){
            int choice=JOptionPane.showConfirmDialog(frame,"File not saved. Save changes?","Warning",JOptionPane.YES_NO_CANCEL_OPTION);
            if(choice==JOptionPane.CANCEL_OPTION){
                return false;
            }else if(choice==JOptionPane.YES_OPTION){
                saveItem.doClick();
                Boolean dirtyAfter=(Boolean)textArea.getClientProperty("dirty");
                if(dirtyAfter!=null && dirtyAfter){
                    return false;
                }
            }
        }
        return true;
    }
    void applyStyle(int styleType) {
        JTextPane textPane = getCurrentTextArea();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        StyledDocument doc = textPane.getStyledDocument();
        if (styleType == 1) {
            boolean isBold = StyleConstants.isBold(currentStyle);
            StyleConstants.setBold(currentStyle, !isBold);
        } else if (styleType == 2) {
            boolean isItalic = StyleConstants.isItalic(currentStyle);
            StyleConstants.setItalic(currentStyle, !isItalic);
        } else if (styleType == 3) {
            boolean isUnderline = StyleConstants.isUnderline(currentStyle);
            StyleConstants.setUnderline(currentStyle, !isUnderline);
        } else if (styleType == 4) { 
            int size = StyleConstants.getFontSize(currentStyle);
            StyleConstants.setFontSize(currentStyle, size + 2);
        } else if (styleType == 5) { 
            int size = StyleConstants.getFontSize(currentStyle);
            StyleConstants.setFontSize(currentStyle, Math.max(8, size - 2));
        }
        doc.setCharacterAttributes(start, end - start, currentStyle, false);
        textPane.setCharacterAttributes(currentStyle, true);
    }
    public static void main(String[] args){
        new NotepadEditor();
    }
}
class LineNumberView extends JTextArea implements DocumentListener {
    JTextPane textPane;
    LineNumberView(JTextPane textPane){
        this.textPane = textPane;
        setEditable(false);
        setBackground(new java.awt.Color(230,230,230));
        textPane.getDocument().addDocumentListener(this);
        updateLineNumbers();
    }
    void updateLineNumbers() {
        int lines = textPane.getDocument().getDefaultRootElement().getElementCount();
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= lines; i++) {
            sb.append(i).append("\n");
        }
        setText(sb.toString());
    }
    public void insertUpdate(DocumentEvent e){ updateLineNumbers(); }
    public void removeUpdate(DocumentEvent e){ updateLineNumbers(); }
    public void changedUpdate(DocumentEvent e){ updateLineNumbers(); }
}