package ifmo.java.idea.git_fast_reword;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnActionEventVisitor;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.VcsLogDataKeys;
import org.jetbrains.annotations.NotNull;
import shchuko.git_fast_reword.GitFastReword;
import shchuko.git_fast_reword.GitOperationFailureException;
import shchuko.git_fast_reword.RepositoryNotFoundException;
import shchuko.git_fast_reword.RepositoryNotOpenedException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Paths;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class IdeaGitFastReword extends AnAction {
    ideaGitFastRewordGUI gitFastRewordGUI;
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        try(GitFastReword gitFastReword = new GitFastReword()) {

            final VcsFullCommitDetails commit =
                    anActionEvent.getRequiredData(VcsLogDataKeys.VCS_LOG).getSelectedDetails().get(0);

            gitFastRewordGUI = new ideaGitFastRewordGUI(commit);
            pressCancel();
            pressOk(gitFastReword, commit);
            listenButtonOk();

        }

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        final VcsFullCommitDetails commit = e.getRequiredData(VcsLogDataKeys.VCS_LOG).getSelectedDetails().get(0);

        e.getPresentation().setEnabled(commit.getParents().size() == 1);
    }

    private void listenButtonOk() {
        gitFastRewordGUI.getText().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                gitFastRewordGUI.getButtonOk().setEnabled(!gitFastRewordGUI.getText().getText().isEmpty());
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                gitFastRewordGUI.getButtonOk().setEnabled(!gitFastRewordGUI.getText().getText().isEmpty());
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                gitFastRewordGUI.getButtonOk().setEnabled(!gitFastRewordGUI.getText().getText().isEmpty());
            }

        });
    }

    private void pressOk(GitFastReword gitFastReword, VcsFullCommitDetails commit) {
        gitFastRewordGUI.getButtonOk().addActionListener(actionEvent -> {
            try {
                gitFastReword.openRepository(Paths.get(commit.getRoot().getPath()));
                gitFastReword.reword(commit.getId().asString(), gitFastRewordGUI.getText().getText());
                gitFastRewordGUI.getJFrame().dispose();
            } catch (RepositoryNotFoundException | IOException | GitOperationFailureException | RepositoryNotOpenedException e) {
                e.printStackTrace();
            }
        });
    }

    private void pressCancel() {
        JFrame jFrame = gitFastRewordGUI.getJFrame();
        gitFastRewordGUI.getButtonCancel().addActionListener(actionEvent -> {
            if (actionEvent.getActionCommand().equals("Cancel")) {
                jFrame.dispose();
            }
        });
    }


}
