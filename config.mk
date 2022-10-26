PRODUCT_SOONG_NAMESPACES += \
    packages/apps/Parts/packages

# Inherit from DeviceSettings config
$(call inherit-product, packages/apps/Parts/configs/devicesettings.mk)

# Inherit from DeviceDoze config
$(call inherit-product, packages/apps/Parts/configs/devicedoze.mk)

# Overlays
PRODUCT_PACKAGES += \
    SettingsOverlayParts

# Sepolicy
SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += packages/apps/Parts/sepolicy/private
SYSTEM_EXT_PUBLIC_SEPOLICY_DIRS += packages/apps/Parts/sepolicy/public
BOARD_VENDOR_SEPOLICY_DIRS += packages/apps/Parts/sepolicy/vendor
