package airboat

import org.eclipse.jgit.diff.DiffEntry

class GitChangedFile {
    String name
    DiffEntry.ChangeType changeType

}
