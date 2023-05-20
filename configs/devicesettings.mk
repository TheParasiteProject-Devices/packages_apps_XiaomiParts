# Device Settings
PRODUCT_PACKAGES += \
    DeviceSettings

PRODUCT_PACKAGES += \
	init.devicesettings.rc \
	init.performance_level.rc

PRODUCT_PACKAGES += \
	privapp-permissions-devicesettings.xml

PRODUCT_SOONG_NAMESPACES += \
    packages/apps/Parts/packages/devicesettings

# Mi Sound FX
PRODUCT_VENDOR_PROPERTIES += \
ro.vendor.audio.misound.bluetooth.enable=true \
ro.vendor.audio.scenario.support=true \
ro.vendor.audio.soundfx.type=true \
ro.vendor.audio.soundfx.usb=true
