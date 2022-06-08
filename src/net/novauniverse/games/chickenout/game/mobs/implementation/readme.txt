To add mobs just make your class implement ChickenOutMobProvider and the plugin will do the rest.

If you want to add mobs in a separate addon plugin make sure you add ChickenOutMobRepo.scanForProviders(this, this.getClass().getPackage().getName()); to your main plugin class

To make a full conversion mod call ChickenOutMobRepo.clearProviders(); when your plugin loads to remove the default mobs