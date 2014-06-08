package de.espend.idea.php.drupal.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectProjectUniqueKeys implements Processor<String> {

    final Project project;
    final ID id;

    final Set<String> stringSet;

    public CollectProjectUniqueKeys(Project project, ID id) {
        this.project = project;
        this.id = id;
        this.stringSet = new HashSet<String>();
    }

    @Override
    public boolean process(String s) {
        this.stringSet.add(s);
        return true;
    }

    public Set<String> getResult() {
        Set<String> set = new HashSet<String>();

        for (String key : stringSet) {
            Collection fileCollection = FileBasedIndex.getInstance().getContainingFiles(id, key, GlobalSearchScope.allScope(this.project));

            if (fileCollection.size() > 0) {
                set.add(key);
            }

        }

        return set;
    }

}
