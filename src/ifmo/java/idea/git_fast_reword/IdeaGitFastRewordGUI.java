package ifmo.java.idea.git_fast_reword;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.vcs.log.VcsFullCommitDetails;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class IdeaGitFastRewordGUI {
    private JButton buttonOk;
    private JButton buttonCancel;
    private JFrame jFrame;
    private JPanel jPanel;
    private JLabel jLabel;
    private JTextArea jTextArea;

    public IdeaGitFastRewordGUI(VcsFullCommitDetails commit){
        setJFrame();
        setJPanel();
        setButtonOK(jPanel);
        setButtonCancel(jPanel);
        setLabel(jPanel, commit);
        setText(jPanel, commit);
        jFrame.add(jPanel);
    }


    private void setJFrame() {
        jFrame = new JFrame("Fast Edit Commit Message");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();

        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader()
                .getResource("Icon/window_border.png"));
        jFrame.setIconImage(image);
        int width = dimension.width / 6;
        int height = dimension.height / 6;
        jFrame.setSize(width, height);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setJPanel() {
        jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
    }

    private void setText(JPanel panel, VcsFullCommitDetails commit) {
        jTextArea = new JTextArea(commit.getFullMessage());
        jTextArea.setLineWrap(true);
        GridBagConstraints constraints = getContain(0.5f, 1, 2, 2, 10, 5);
        jTextArea.setBorder(JBUI.Borders.empty(5, 10));
        constraints.fill = GridBagConstraints.BOTH;
        JScrollPane jScroll = new JBScrollPane(jTextArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(jScroll, constraints);
    }


    private void setLabel(JPanel panel, VcsFullCommitDetails commit) {
        final String text ="Edit message for commit " + commit.getId().toShortString() + " by "
                + commit.getAuthor().getName();
        jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(jLabel, getContain(0, 0, 1, 1, 12, 1));
    }


    private void setButtonOK(JPanel panel) {
        buttonOk = new JButton("OK");
        buttonOk.setBackground(JBColor.blue);
        panel.add(buttonOk, getContain(0.5f, 0, 8, 6, 2, 1));
    }

    private void setButtonCancel(JPanel panel) {
        buttonCancel = new JButton("Cancel");
        buttonCancel.setName("Cancel");
        panel.add(buttonCancel, getContain(0.5f, 0, 8, 10, 2, 1));
    }

    private GridBagConstraints getContain(float weightx, float weighty, int gridy, int gridx, int gridwidth,
                                          int gridheight) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.gridy = gridy;
        constraints.gridx = gridx;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        return constraints;
    }

    public JFrame getJFrame() {
        return jFrame;
    }

    public JPanel getjPanel() {
        return jPanel;
    }

    public JButton getButtonOk() {
        return buttonOk;
    }

    public JButton getButtonCancel() {
        return buttonCancel;
    }

    public JLabel getjLabel() {
        return jLabel;
    }

    public JTextArea getText() {
        return jTextArea;
    }

}
