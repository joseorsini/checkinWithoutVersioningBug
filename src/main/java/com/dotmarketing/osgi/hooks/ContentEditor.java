package com.dotmarketing.osgi.hooks;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Permission;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.UserAPI;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.business.DotContentletStateException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.fileassets.business.FileAssetAPI;
import com.dotmarketing.portlets.languagesmanager.business.LanguageAPI;
import com.dotmarketing.portlets.structure.model.Relationship;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;

public class ContentEditor {
    
    public void EditFileAsset(){
        CategoryAPI catAPI = APILocator.getCategoryAPI();
        ContentletAPI conAPI = APILocator.getContentletAPI();
        FileAssetAPI faAPI = APILocator.getFileAssetAPI();
        LanguageAPI langAPI = APILocator.getLanguageAPI();
        PermissionAPI perAPI = APILocator.getPermissionAPI();
        UserAPI userAPI = APILocator.getUserAPI();
        //String contentletIdentifier = "493c9255-461c-4a1b-accf-78bd5c334d84";
        String contentletIdentifier = "48424c6a-530b-4a51-ba10-b69ab93b4fd3";
        
        try {
            Logger.info(this, "Looking for content with Identifier: " + contentletIdentifier);
            Contentlet con = conAPI.findContentletByIdentifier(contentletIdentifier, true, 
                    langAPI.getDefaultLanguage().getId(), userAPI.getSystemUser(), false);
            if (con!=null && InodeUtils.isSet(con.getInode())){
                
                String filePath = faAPI.getRealAssetsRootPath() + java.io.File.separator 
                        + "our_file" + java.io.File.separator + "0020.jpg";
                
                Logger.info(this, "finding new file to assign to content, under path: " + filePath);
                
                File newFile = new File(filePath);
                
                if (newFile.exists()){
                    Logger.info(this, "File was found: " + newFile.getPath());
                    con.setBinary("file", newFile);

                } else {
                    Logger.info(this, "Something happened! Check your file and see if you're able to find it");   
                }
                con.setStringProperty("title", con.getTitle() + " updated");
                List<Permission> permissions = perAPI.getPermissions(con);
                List<Category> cats = catAPI.getParents(con, userAPI.getSystemUser(), true);
                Map <Relationship, List<Contentlet>>contentRelationships = conAPI.findContentRelationships(con, userAPI.getSystemUser());
                
                con = conAPI.checkinWithoutVersioning(con, contentRelationships, cats, permissions, userAPI.getSystemUser(), false);

                Logger.info(this, "We're done!");
                
            }
        } catch (DotContentletStateException e) {
            Logger.error(this, "Something happened - 001", e);
        } catch (DotDataException e) {
            Logger.error(this, "Something happened - 002", e);
        } catch (DotSecurityException e) {
            Logger.error(this, "Something happened - 003", e);
        } catch (IOException e) {
            Logger.error(this, "Something happened - 004", e);
        }
    }
}