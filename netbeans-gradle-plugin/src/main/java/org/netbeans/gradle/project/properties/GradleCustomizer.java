package org.netbeans.gradle.project.properties;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jtrim.utils.ExceptionHelper;
import org.netbeans.gradle.project.NbGradleProject;
import org.netbeans.gradle.project.NbStrings;
import org.netbeans.gradle.project.api.entry.GradleProjectIDs;
import org.netbeans.gradle.project.others.ChangeLFPlugin;
import org.netbeans.gradle.project.properties.ui.CommonProjectPropertiesPanel;
import org.netbeans.gradle.project.properties.ui.CustomVariablesPanel;
import org.netbeans.gradle.project.properties.ui.LicenseHeaderPanel;
import org.netbeans.gradle.project.properties.ui.ManageBuiltInTasksPanel;
import org.netbeans.gradle.project.properties.ui.ManageTasksPanel;
import org.netbeans.gradle.project.properties.ui.ProjectAppearancePanel;
import org.netbeans.modules.editor.indent.project.api.Customizers;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.implspi.NamedServicesProvider;

public final class GradleCustomizer implements CustomizerProvider {
    private static final Logger LOGGER = Logger.getLogger(GradleCustomizer.class.getName());

    private static final String GRADLE_CATEGORY_NAME = GradleCustomizer.class.getName() + ".gradle";
    private static final String BUILT_IN_TASKS_CATEGORY_NAME = GradleCustomizer.class.getName() + ".gradle-built-in-commands";
    private static final String CUSTOM_TASKS_CATEGORY_NAME = GradleCustomizer.class.getName() + ".gradle-custom-tasks";
    private static final String LICENSE_CATEGORY_NAME = GradleCustomizer.class.getName() + ".gradle-license";
    private static final String APPEARANCE_CATEGORY_NAME = GradleCustomizer.class.getName() + ".appearance";
    private static final String CUSTOM_VARIABLES_CATEGORY_NAME = GradleCustomizer.class.getName() + ".customVars";

    private final NbGradleProject project;

    public GradleCustomizer(NbGradleProject project) {
        ExceptionHelper.checkNotNullArgument(project, "project");
        this.project = project;
    }

    private static ProjectCustomizer.CompositeCategoryProvider[] getExternalCustomizers() {
        List<ProjectCustomizer.CompositeCategoryProvider> result
                = new LinkedList<>();

        result.add(Customizers.createFormattingCategoryProvider(Collections.emptyMap()));

        ProjectCustomizer.CompositeCategoryProvider changeLFProperties = ChangeLFPlugin.getProjectSettings();
        if (changeLFProperties != null) {
            result.add(changeLFProperties);
        }

        return result.toArray(new ProjectCustomizer.CompositeCategoryProvider[result.size()]);
    }

    private static ProfileBasedCustomizer newMainCustomizer(NbGradleProject project) {
        return new ProfileBasedCustomizer(
                GRADLE_CATEGORY_NAME,
                NbStrings.getGradleProjectCategoryName(),
                CommonProjectPropertiesPanel.createProfileBasedPanel(project));
    }

    private static ProfileBasedCustomizer newBuiltInTasksCustomizer(NbGradleProject project) {
        return new ProfileBasedCustomizer(
                BUILT_IN_TASKS_CATEGORY_NAME,
                NbStrings.getManageBuiltInTasksTitle(),
                ManageBuiltInTasksPanel.createProfileBasedPanel(project));
    }

    private static ProfileBasedCustomizer newCustomTasksCustomizer(NbGradleProject project) {
        return new ProfileBasedCustomizer(
                CUSTOM_TASKS_CATEGORY_NAME,
                NbStrings.getManageCustomTasksTitle(),
                ManageTasksPanel.createProfileBasedPanel(project));
    }

    private static ProfileBasedCustomizer newLicenseCustomizer(NbGradleProject project) {
        return new ProfileBasedCustomizer(
                LICENSE_CATEGORY_NAME,
                NbStrings.getGradleProjectLicenseCategoryName(),
                LicenseHeaderPanel.createProfileBasedPanel(project));
    }

    private static ProfileBasedCustomizer newAppearanceCustomizer(NbGradleProject project) {
        return new ProfileBasedCustomizer(
                APPEARANCE_CATEGORY_NAME,
                NbStrings.getAppearanceCategoryName(),
                ProjectAppearancePanel.createProfileBasedPanel(project));
    }

    private static ProfileBasedCustomizer newCustomVariablesCustomizer(NbGradleProject project) {
        return new ProfileBasedCustomizer(
                CUSTOM_VARIABLES_CATEGORY_NAME,
                NbStrings.getCustomVariablesCategoryName(),
                CustomVariablesPanel.createProfileBasedPanel(project));
    }

    private static Collection<? extends ProjectCustomizer.CompositeCategoryProvider> getAnnotationBasedProviders() {
        Lookup customizerLookup = NamedServicesProvider.forPath("Projects/" + GradleProjectIDs.MODULE_NAME + "/Customizer");
        return customizerLookup.lookupAll(ProjectCustomizer.CompositeCategoryProvider.class);
    }

    private void getCustomizersOfExtensions(List<ProjectCustomizer.CompositeCategoryProvider> result) {
        Collection<? extends ExtensionProjectSettingsPageDefs> defs
                = project.getCombinedExtensionLookup().lookupAll(ExtensionProjectSettingsPageDefs.class);
        for (ExtensionProjectSettingsPageDefs def: defs) {
            result.addAll(def.getCustomizers());
        }
    }

    private ProjectCustomizer.CompositeCategoryProvider[] getAllCustomizers() {
        ProjectCustomizer.CompositeCategoryProvider[] externalCategories
                = getExternalCustomizers();
        List<ProjectCustomizer.CompositeCategoryProvider> allCategoriesList
                = new ArrayList<>(externalCategories.length + 2);

        allCategoriesList.add(newMainCustomizer(project));
        allCategoriesList.add(newBuiltInTasksCustomizer(project));
        allCategoriesList.add(newCustomTasksCustomizer(project));
        allCategoriesList.add(newCustomVariablesCustomizer(project));
        allCategoriesList.add(newLicenseCustomizer(project));
        getCustomizersOfExtensions(allCategoriesList);
        allCategoriesList.add(newAppearanceCustomizer(project));
        allCategoriesList.addAll(Arrays.asList(externalCategories));
        allCategoriesList.addAll(getAnnotationBasedProviders());

        return allCategoriesList.toArray(new ProjectCustomizer.CompositeCategoryProvider[allCategoriesList.size()]);
    }

    @Override
    public void showCustomizer() {
        final Lookup lookup = Lookups.fixed(project);

        final ProjectCustomizer.CompositeCategoryProvider[] customizers
                = getAllCustomizers();

        final ProjectCustomizer.Category[] categories =
                new ProjectCustomizer.Category[customizers.length];
        for (int i = 0; i < categories.length; i++) {
            categories[i] = customizers[i].createCategory(lookup);
        }

        ProjectCustomizer.CategoryComponentProvider panelProvider = new ProjectCustomizer.CategoryComponentProvider() {
            @Override
            public JComponent create(ProjectCustomizer.Category category) {
                String name = category.getName();
                if (name == null) {
                    LOGGER.log(Level.WARNING, "null category name.");
                    return new JPanel();
                }

                for (int i = 0; i < categories.length; i++) {
                    if (name.equals(categories[i].getName())) {
                        return customizers[i].createComponent(category, lookup);
                    }
                }

                LOGGER.log(Level.WARNING, "Requested category cannot be found {0}.", name);
                return new JPanel();
            }
        };

        ActionListener okListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // no-op
            }
        };

        Dialog dlg = ProjectCustomizer.createCustomizerDialog(
                categories,
                panelProvider,
                GRADLE_CATEGORY_NAME,
                okListener,
                HelpCtx.DEFAULT_HELP);

        dlg.setTitle(NbStrings.getProjectPropertiesDlgTitle(project.displayName().getValue()));
        dlg.setModal(true);
        dlg.setVisible(true);
    }

}
