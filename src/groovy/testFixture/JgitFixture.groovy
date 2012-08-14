package testFixture

class JgitFixture {

    static final String CORRECT_GIT_DIF_OUTPUT = """
        diff --git a/grails-app/controllers/codereview/ChangesetController.groovy b/grails-app/controllers/codereview/ChangesetController.groovy
index 0879401..7df3f5b 100644
--- a/grails-app/controllers/codereview/ChangesetController.groovy
+++ b/grails-app/controllers/codereview/ChangesetController.groovy
@@ -36,8 +36,8 @@ class ChangesetController {
                     id: changeset.id,
                     identifier: changeset.identifier,
                     author: changeset.commiter.cvsCommiterId,
-                    email: changeset.commiter.user?.email,
-                    date: changeset.date,
+                    email: getUserEmail(changeset),
+                    date: changeset.date.format("yyyy-MM-dd HH:mm"),
                     commitComment: changeset.commitComment,
                     commentsCount: changeset.commentsCount,
                     projectName: changeset.project.name,
@@ -47,6 +47,16 @@ class ChangesetController {
         render changesetProperties as JSON
     }

+    private String getUserEmail(Changeset changeset) {
+        def user = changeset.commiter.user
+        if (user == null) {
+            //TODO check if the assumption that committers not always have email holds. If not, use commiter.email
+            'no.such.email@codereview.touk.pl'
+        } else {
+            user.email
+        }
+    }
+
     @VisibleForTesting
     boolean belongsToCurrentUser(Changeset changeset) {
         authenticatedUser != null && authenticatedUser == changeset.commiter?.user
diff --git a/grails-app/views/changeset/index.gsp b/grails-app/views/changeset/index.gsp
index eac8a0e..b56440f 100644
--- a/grails-app/views/changeset/index.gsp
+++ b/grails-app/views/changeset/index.gsp
+        }
+    }
+
     @VisibleForTesting
     boolean belongsToCurrentUser(Changeset changeset) {
         authenticatedUser != null && authenticatedUser == changeset.commiter?.user

        """
}
