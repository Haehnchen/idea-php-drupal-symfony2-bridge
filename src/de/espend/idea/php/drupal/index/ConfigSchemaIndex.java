package de.espend.idea.php.drupal.index;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import fr.adrienbrault.idea.symfony2plugin.Symfony2ProjectComponent;
import fr.adrienbrault.idea.symfony2plugin.stubs.indexes.ServicesDefinitionStubIndex;
import fr.adrienbrault.idea.symfony2plugin.util.PsiElementUtils;
import fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper;
import gnu.trove.THashMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ConfigSchemaIndex extends FileBasedIndexExtension<String, String[]> {

    public static final ID<String, String[]> KEY = ID.create("de.espend.idea.php.drupal.config_schema");
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, String[]> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, String[], FileContent> getIndexer() {
        return inputData -> {
            Map<String, String[]> map = new THashMap<String, String[]>();

            PsiFile psiFile = inputData.getPsiFile();
            if(!Symfony2ProjectComponent.isEnabledForIndex(psiFile.getProject())) {
                return map;
            }

            if(!(psiFile instanceof YAMLFile) || !isValidForIndex(inputData, psiFile)) {
                return map;
            }

            for(YAMLKeyValue yamlKeyValue: YamlHelper.getTopLevelKeyValues((YAMLFile) psiFile)) {
                String key = PsiElementUtils.trimQuote(yamlKeyValue.getKeyText());

                if(StringUtils.isBlank(key) || key.contains("*")) {
                    continue;
                }

                Collection<String> mappings = new ArrayList<String>();
                YAMLKeyValue mapping = YamlHelper.getYamlKeyValue(yamlKeyValue, "mapping");
                if(mapping == null) {
                    continue;
                }

                Set<String> keySet = YamlHelper.getKeySet(mapping);
                if(keySet != null) {
                    mappings.addAll(keySet);
                }

                map.put(key, mappings.toArray(new String[mappings.size()]));
            }

            return map;
        };

    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<String[]> getValueExternalizer() {
        return new ServicesDefinitionStubIndex.MySetDataExternalizer();
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file ->
            file.getFileType() == YAMLFileType.YML || file.getFileType() == XmlFileType.INSTANCE;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 2;
    }

    public static boolean isValidForIndex(FileContent inputData, PsiFile psiFile) {

        String fileName = psiFile.getName();
        if(fileName.startsWith(".") || !fileName.endsWith(".schema.yml")) {
            return false;
        }

        return true;
    }
}



