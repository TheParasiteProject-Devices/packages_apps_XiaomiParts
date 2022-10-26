PARTS_PATH := packages/apps/Parts

PRODUCT_SOONG_NAMESPACES += \
    $(PARTS_PATH)

# Inherit from DeviceSettings config
$(call inherit-product, $(PARTS_PATH)/configs/devicesettings.mk)

# Inherit from DeviceDoze config
$(call inherit-product, $(PARTS_PATH)/configs/devicedoze.mk)

# Overlays
PRODUCT_PACKAGES += \
    SettingsOverlayParts

# Sepolicy
SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += $(PARTS_PATH)/sepolicy/private
SYSTEM_EXT_PUBLIC_SEPOLICY_DIRS += $(PARTS_PATH)/sepolicy/public
BOARD_VENDOR_SEPOLICY_DIRS += $(PARTS_PATH)/sepolicy/vendor
