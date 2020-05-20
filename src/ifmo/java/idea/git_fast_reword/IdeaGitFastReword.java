package ifmo.java.idea.git_fast_reword;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.*;
import git4idea.GitUtil;
import git4idea.i18n.GitBundle;
import org.jetbrains.annotations.NotNull;
import shchuko.git_fast_reword.GitFastReword;
import shchuko.git_fast_reword.GitOperationFailureException;
import shchuko.git_fast_reword.RepositoryNotFoundException;
import shchuko.git_fast_reword.RepositoryNotOpenedException;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

public class IdeaGitFastReword extends AnAction {
    IdeaGitFastRewordGUI gitFastRewordGUI;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        final VcsFullCommitDetails commit =
                anActionEvent.getRequiredData(VcsLogDataKeys.VCS_LOG).getSelectedDetails().get(0);

        gitFastRewordGUI = new IdeaGitFastRewordGUI(commit);
        createCancelButtonListener();
        createOkButtonListener(commit);
        listenButtonOk();
        createEscapeListener();
        createOkListener(commit);
        gitFastRewordGUI.getText().requestFocusInWindow();

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        final VcsFullCommitDetails commit = e.getRequiredData(VcsLogDataKeys.VCS_LOG).getSelectedDetails().get(0);


        Project project = e.getProject();
        VcsLog log = e.getData(VcsLogDataKeys.VCS_LOG);
        VcsLogDataProvider data = e.getData(VcsLogDataKeys.VCS_LOG_DATA_PROVIDER);
        VcsLogUi ui = e.getData(VcsLogDataKeys.VCS_LOG_UI);
        if (project == null || log == null || data == null || ui == null) {
            e.getPresentation().setEnabled(false);
            return;
        }

        e.getPresentation().setEnabled(commit.getParents().size() == 1);

        /*GitRepositoryManager repositoryManager = getRepositoryManager(project);
        GitRepository repository = repositoryManager.getRepositoryForRootQuick(commit.getRoot());
        if (repository == null || repositoryManager.isExternal(repository)) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
*/
        // editing merge commit or root commit is not allowed
        int parents = commit.getParents().size();
        if (parents != 1) {
            e.getPresentation().setEnabled(false);
            e.getPresentation().setDescription("rebase.log.commit.editing.action.disabled.parents.description: " + parents);
            return;
        }

        // allow editing only in the current branch
        Collection<String> branches = log.getContainingBranches(commit.getId(), commit.getRoot());
        if (branches != null) { // otherwise the information is not available yet, and we'll recheck harder in actionPerformed
            if (!branches.contains(GitUtil.HEAD)) {
                e.getPresentation().setEnabled(false);
                e.getPresentation().setDescription("Rebase.log.commit.editing.action.commit.not.in.head.error.text");
                return;
            }

           /* // and not if pushed to a protected branch
            String protectedBranch = findProtectedRemoteBranch(repository, branches);
            if (protectedBranch != null) {
                e.getPresentation().setEnabledAndVisible(false);
                e.getPresentation().setDescription(commitPushedToProtectedBranchError(protectedBranch));
                return;
            }*/
        }


        e.getPresentation().setEnabled(true);
    }

    protected String commitPushedToProtectedBranchError(String protectedBranch) {
        return GitBundle.message("rebase.log.commit.editing.action.commit.pushed.to.protected.branch.error.text: "
                + protectedBranch);
    }

    private void createEscapeListener() {
        gitFastRewordGUI.getText().addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gitFastRewordGUI.getJFrame().dispose();
                }
            }

        });
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

    private void createOkButtonListener(VcsFullCommitDetails commit) {
        gitFastRewordGUI.getButtonOk().addActionListener(actionEvent -> rewordMessage(commit));
    }

    private void rewordMessage(VcsFullCommitDetails commit) {
        try (GitFastReword gitFastReword = new GitFastReword()) {
            gitFastReword.openRepository(Paths.get(commit.getRoot().getPath()));
            if (commit.getFullMessage().equals(gitFastRewordGUI.getText().getText())) {
                return;
            }
            gitFastReword.reword(commit.getId().asString(), gitFastRewordGUI.getText().getText());
            throw new GitOperationFailureException("Some text");
        } catch (RepositoryNotFoundException | IOException | GitOperationFailureException |
                RepositoryNotOpenedException e) {
            Notifications.Bus.notify(new Notification("git-fast-reword", "IdeaGitFastRewordPlugin",
                    e.getMessage() , NotificationType.ERROR));
            e.printStackTrace();
        }finally {
            gitFastRewordGUI.getJFrame().dispose();
        }
    }

    private void createCancelButtonListener() {
        gitFastRewordGUI.getButtonCancel().addActionListener(actionEvent -> {
            if (actionEvent.getActionCommand().equals("Cancel")) {
                gitFastRewordGUI.getJFrame().dispose();
            }
        });
    }

    private void createOkListener(VcsFullCommitDetails commit) {
        gitFastRewordGUI.getText().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (KeyEvent.VK_ENTER == keyEvent.getKeyCode()) {
                    rewordMessage(commit);
                }
            }
        });
    }

}
