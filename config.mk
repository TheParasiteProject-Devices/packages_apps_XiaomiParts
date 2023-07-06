PRODUCT_SOONG_NAMESPACES += \
    packages/apps/XiaomiParts/packages

# Inherit from DeviceSettings config
$(call inherit-product, packages/apps/XiaomiParts/configs/devicesettings.mk)

# Inherit from DeviceDoze config
$(call inherit-product, packages/apps/XiaomiParts/configs/devicedoze.mk)

# Overlays
PRODUCT_PACKAGES += \
    SettingsOverlayParts

# Sepolicy
SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += packages/apps/XiaomiParts/sepolicy/private
SYSTEM_EXT_PUBLIC_SEPOLICY_DIRS += packages/apps/XiaomiParts/sepolicy/public
BOARD_VENDOR_SEPOLICY_DIRS += packages/apps/XiaomiParts/sepolicy/vendor
