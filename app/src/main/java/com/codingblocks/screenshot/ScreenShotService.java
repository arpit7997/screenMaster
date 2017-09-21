package com.codingblocks.screenshot;

import com.luseen.screenshotobserver.ScreenshotObserverService;

/**
 * Created by mayankchauhan on 13/09/17.
 */

public class ScreenShotService extends ScreenshotObserverService  {
    @Override
    protected void onScreenShotTaken(String path, String fileName) {
        OnClickActivity.onScreenShotTaken(this);
    }
}
