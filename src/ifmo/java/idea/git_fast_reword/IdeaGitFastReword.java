package ifmo.java.idea.git_fast_reword;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.VcsLog;
import com.intellij.vcs.log.VcsLogDataKeys;
import com.intellij.vcs.log.VcsLogDataProvider;
import git4idea.GitUtil;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryFiles;
import org.jetbrains.annotations.NotNull;
import shchuko.git_fast_reword.GitFastReword;
import git4idea.log.GitShowCommitInLogAction;

import static git4idea.GitUtil.getRepositoryManager;

public class IdeaGitFastReword extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        try(GitFastReword gitFastReword = new GitFastReword()) {

            Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
            VcsLogDataProvider data = anActionEvent.getRequiredData(VcsLogDataKeys.VCS_LOG_DATA_PROVIDER);
            VcsLog log = anActionEvent.getRequiredData(VcsLogDataKeys.VCS_LOG);
            final VcsFullCommitDetails commit = anActionEvent.getRequiredData(VcsLogDataKeys.VCS_LOG).getSelectedDetails().get(0);
            //final GitRepository gitRepository = getRepositoryManager(project).getRepositoryForRoot(commit.getRoot());
            //commit.getId().asString()
            /*Notification notification = new Notification("fast-reword", "Fast-reword", commit.getRoot().getPath(),
                    NotificationType.INFORMATION);
            Notifications.Bus.notify(notification);
*/


        }
    }
}
