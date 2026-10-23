# CampusFind prototype implementation contract — 2026-10-23

This file is the source of truth for reproducing the CampusFind personal dashboard, guided **Find My Item** conversation, and **My Claims** list. Follow the behavior and structure rules first, then use the exact source appendix. The appendix is copied from the live prototype. When a later user request conflicts with this file, the later request wins.

## Scope

The prototype contains two separate Activities:

- `CurveTestActivity`: the launcher dashboard with the animated CampusFind pet and **Find My Item** action.
- `ClaimsTestActivity`: the separate **My Claims** RecyclerView page.

Keep both screens separate. Do not merge the claims list into the dashboard and do not replace the guided chat with a separate Activity. The guided chat belongs in a 90%-height `BottomSheetDialog` opened from the dashboard action.

## Required implementation rules

- Use Java, Android XML, ViewBinding, AndroidX, Material Components, and local offline resources only.
- Keep static visible copy inline in XML. Runtime conversation text and sample data belong in Java.
- Use the existing Arial Rounded font through the app theme and use `typography.xml` dimensions instead of raw `sp` values.
- Put screen classes in the base package, models in `.models`, adapters in `.adapters`, and reusable helpers in `.helpers`.
- Use a RecyclerView, model, adapter, ViewHolder, and row XML for both the chat transcript and claims list.
- Adapter constructors take only `Context` and the model list.
- Reset visibility for every variable row element during `onBindViewHolder` so recycled chat rows never show the wrong message type.
- Keep the prototype offline. Do not add Internet permission, remote APIs, cloud authentication, or image-loading dependencies.
- Keep `CurveTestActivity` as the launcher while demonstrating the Find My Item flow. Register `ClaimsTestActivity` separately with the Personal theme.
- Copy `app/src/main/res/drawable-nodpi/campus_helper_pet.png`. It is a required binary asset and cannot be represented in this Markdown file.
- Do not add a bottom navigation bar to either screen.

## Exact palette

| Token | Value | Use |
| --- | --- | --- |
| `campus_background_start` | `#0B3DA6` | Deep blue page/background gradient |
| `campus_background_end` | `#1E6BFF` | Bright blue page/background gradient |
| `campus_wave_cyan` | `#3EC5FF` | Cyan waves and Find logo text |
| `campus_wave_indigo` | `#8A5CFF` | Purple waves |
| `campus_button_start` | `#2EC5FF` | CTA and user-message gradient start |
| `campus_button_end` | `#3A3DFF` | CTA and user-message gradient end |
| `campus_text_secondary` | `#AFC9FF` | Supporting text on blue |
| `campus_text_tertiary` | `#8FB0E8` | Muted text on blue |
| `campus_speech_text` | `#1B2D8F` | Speech-bubble text |
| `campus_glass` | `#1AFFFFFF` | 10% white glass background |
| `campus_glass_border` | `#40FFFFFF` | Glass outline |
| `campus_orb_pink` | `#FFB6E6` | Decorative orb |
| `claim_pending_background` | `#FFF0AA` | Pending badge |
| `claim_pending_text` | `#A96D00` | Pending label |
| `claim_approved_background` | `#75F1C5` | Approved badge |
| `claim_approved_text` | `#087A59` | Approved label |
| `claim_rejected_background` | `#FF91AE` | Rejected badge |
| `claim_rejected_text` | `#B60A45` | Rejected label |
| `claim_returned_background` | `#9BDCFB` | Returned badge |
| `claim_returned_text` | `#164BBD` | Returned label |

Do not replace these colors with the generic green authentication palette. The CampusFind prototype is a Personal-role experience and uses its own blue visual system.

## Dashboard design

Use `activity_curve_test.xml` exactly as the structural reference:

- Full-screen `campus_dashboard_background` abstract blue vector.
- CampusFind wordmark with white “Campus” and cyan “Find”.
- White notification bell and translucent circular initials avatar.
- White greeting, light-blue date, and translucent claims count card.
- Existing transparent CampusFind pet centered in the flexible middle area.
- White changing speech bubble above the pet using a `ViewFlipper` at a 2400 ms interval.
- Full-width cyan-to-indigo **Find My Item** button.
- The pet uses one short upward `translationY` jump followed by a pause, repeating every 2000 ms.

Keep status and navigation bars deep blue with light system icons. Do not stretch the pet bitmap as a page background.

## Guided Find My Item chat

The dashboard action opens `bottom_sheet_find_item.xml` in an expanded `BottomSheetDialog` at about 90% of screen height. The sheet contains a fixed header, RecyclerView transcript, and fixed composer.

Conversation order:

1. Pet: `Did you lose an item?`
2. User replies `Yes`.
3. Pet: `What is the name of the item you lost?`
4. User enters the item name.
5. Pet: `Could you please describe it?`
6. User enters a description.
7. Pet: `Where did you lose it?`
8. User enters a location.
9. Pet: `When did you lose it?`
10. Open a native `DatePickerDialog`; add the selected date as the user response.
11. Search the demo matches.

The demo has two deterministic branches:

- If the item name contains `wallet` and the location contains `cafeteria`, show the two match cards: **Black Wallet** and **Checkered Wallet**. Then ask: `Is one of these yours? Reply not if neither item matches.` Keep the composer active.
- Any response containing `not` after the match list enters the rejected-match path. Ask for **exactly 3 clear photos**. Enable the attachment action and disable text entry. Accept only exactly three selected images, then show the complete report summary and request confirmation.
- Any other item/location combination is the direct no-match demo. Ask for at least two and at most three photos. After a valid selection, show the same report summary and confirmation step.
- If the user accepts a suggested match, tell them to open the matching item and continue the claim.

Before creating any no-match lost report, the pet must display a summary containing the item name, description, location, date lost, and number of attached photos. Then ask: `Submit this lost report? Reply Yes to submit or No to cancel.` Only `Yes` or `Y` submits the report. `No`, `not`, or `cancel` leaves it unsubmitted. Any other response repeats the confirmation request.

Use `ActivityResultContracts.GetMultipleContents()` with MIME type `image/*`. The rejected-match route validates exactly three images. The direct no-match route validates two or three. Use `AppToast.error` for an invalid count. Do not request storage permission.

The transcript uses three message types:

- `PET`: left-aligned pet avatar and white bubble.
- `USER`: right-aligned cyan-to-indigo bubble.
- `MATCHES`: left-aligned pet avatar with a white results container and two compact match cards.

## My Claims list

`ClaimsTestActivity` is a separate Activity using `activity_claims_test.xml` and `item_claim_test.xml`.

- Keep the blue abstract background and CampusFind header.
- Show the back button, notification bell, and MJ avatar.
- Show **My Claims**, its supporting line, and the live count card.
- Bind the count from the actual seeded list. The current demonstration seeds 16 rows so the count is 16.
- Use a vertical RecyclerView with newest sample claims first.
- Each row uses a translucent glass card, rounded illustration panel, item name, location, activity/date line, status badge, and chevron.
- Status colors are semantic and must reset on every bind: Pending yellow, Approved mint, Rejected pink, Returned blue.
- Use the included local vector illustrations for wallet, student ID, phone, keys, and water bottle.

## Required project structure

```text
app/src/main/java/com/example/restaurantapp/
├── CurveTestActivity.java
├── ClaimsTestActivity.java
├── adapters/
│   ├── FindChatAdapter.java
│   └── ClaimTestAdapter.java
├── models/
│   ├── FindChatMessage.java
│   └── ClaimTestItem.java
└── helpers/
    └── AppToast.java

app/src/main/res/
├── drawable-nodpi/campus_helper_pet.png
├── layout/activity_curve_test.xml
├── layout/bottom_sheet_find_item.xml
├── layout/item_find_chat_message.xml
├── layout/activity_claims_test.xml
├── layout/item_claim_test.xml
└── drawable/...
```

## Manifest rules

Assign `@style/Theme.RestaurantApp.Personal` to both Activities. `CurveTestActivity` owns the `MAIN`/`LAUNCHER` intent filter during this prototype. `ClaimsTestActivity` is not exported.

## Exact source appendix

The following files are the current implementation. Copy them to the same relative paths when reproducing this prototype. Existing compatible shared resources may remain shared, but their visible values must match these files.

### `app/src/main/res/values/colors.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary_darkest">#062F1D</color>
    <color name="primary_dark">#08783F</color>
    <color name="primary">#25CE99</color>
    <color name="primary_lime">#BEF264</color>
    <color name="primary_light">#E6F7EE</color>

    <color name="admin_primary">#18181B</color>
    <color name="admin_primary_dark">#000000</color>
    <color name="admin_primary_light">#F1F1F2</color>
    <color name="admin_accent">#71717A</color>
    <color name="admin_page_background">#F7F7F8</color>
    <color name="admin_overlay_start">#E6000000</color>
    <color name="admin_overlay_end">#99000000</color>

    <color name="personal_primary">#3B82F6</color>
    <color name="personal_primary_dark">#1D4ED8</color>
    <color name="personal_primary_light">#DBEAFE</color>
    <color name="personal_accent">#60A5FA</color>
    <color name="personal_page_background">#F5F8FF</color>
    <color name="personal_overlay_start">#D9172554</color>
    <color name="personal_overlay_end">#993B82F6</color>

    <color name="campus_background_start">#0B3DA6</color>
    <color name="campus_background_end">#1E6BFF</color>
    <color name="campus_wave_cyan">#3EC5FF</color>
    <color name="campus_wave_indigo">#8A5CFF</color>
    <color name="campus_button_start">#2EC5FF</color>
    <color name="campus_button_end">#3A3DFF</color>
    <color name="campus_text_secondary">#AFC9FF</color>
    <color name="campus_text_tertiary">#8FB0E8</color>
    <color name="campus_speech_text">#1B2D8F</color>
    <color name="campus_glass">#1AFFFFFF</color>
    <color name="campus_glass_border">#40FFFFFF</color>
    <color name="campus_orb_pink">#FFB6E6</color>
    <color name="claim_pending_background">#FFF0AA</color>
    <color name="claim_pending_text">#A96D00</color>
    <color name="claim_approved_background">#75F1C5</color>
    <color name="claim_approved_text">#087A59</color>
    <color name="claim_rejected_background">#FF91AE</color>
    <color name="claim_rejected_text">#B60A45</color>
    <color name="claim_returned_background">#9BDCFB</color>
    <color name="claim_returned_text">#164BBD</color>

    <color name="success">#16B981</color>
    <color name="destructive">#EF4260</color>

    <color name="badge_green">#25CE99</color>
    <color name="badge_red">#BC3048</color>
    <color name="badge_yellow">#F9A93D</color>

    <color name="text_heading">#101512</color>
    <color name="text_body">#526572</color>
    <color name="text_subheading">#84918A</color>



    <color name="white">#FFFFFFFF</color>
    <color name="auth_sheet_translucent">#E6FFFFFF</color>
    <color name="transparent">#00000000</color>
    <color name="white_20">#33FFFFFF</color>
    <color name="white_25">#40FFFFFF</color>
    <color name="white_38">#60FFFFFF</color>
    <color name="black_27">#44000000</color>
    <color name="black_45">#73000000</color>
    <color name="black_75">#BF000000</color>
    <color name="success_9">#1816B981</color>

    <color name="field_background">#F8F8F8</color>
    <color name="field_border">#E9E9E9</color>
    <color name="field_text">#171A18</color>
    <color name="field_hint">#999D9A</color>
    <color name="field_icon">#858B87</color>
    <color name="field_error">#C83F55</color>
    <color name="field_upload_border">#C8CECB</color>
    <color name="search_background">#F5F6F8</color>
    <color name="search_border">#E8ECE9</color>
    <color name="search_text">#142119</color>
    <color name="search_hint">#A7ABA8</color>
    <color name="create_page_background">#F5F6F5</color>
    <color name="activity_show_background">#F7FAF8</color>
    <color name="page_divider">#ECEEEC</color>
</resources>

```

### `app/src/main/res/values/typography.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="type_micro">9sp</dimen>
    <dimen name="type_caption">10sp</dimen>
    <dimen name="type_detail">11sp</dimen>
    <dimen name="type_label">12sp</dimen>
    <dimen name="type_subheading">13sp</dimen>
    <dimen name="type_body">14sp</dimen>
    <dimen name="type_button">15sp</dimen>
    <dimen name="type_body_large">16sp</dimen>
    <dimen name="type_person_name">17sp</dimen>
    <dimen name="type_card_heading">18sp</dimen>
    <dimen name="type_card_heading_large">19sp</dimen>
    <dimen name="type_section_heading">20sp</dimen>
    <dimen name="type_report_section">21sp</dimen>
    <dimen name="type_page_heading">22sp</dimen>
    <dimen name="type_large_heading">24sp</dimen>
    <dimen name="type_report_title">25sp</dimen>
    <dimen name="type_metric">26sp</dimen>
    <dimen name="type_dashboard_greeting">27sp</dimen>
    <dimen name="type_display">28sp</dimen>
    <dimen name="type_modal_symbol">36sp</dimen>
    <dimen name="type_report_total">46sp</dimen>
    <dimen name="type_auth_hero">34sp</dimen>
</resources>

```

### `app/src/main/res/values/themes.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources
    xmlns:tools="http://schemas.android.com/tools">
    <style
        name="Base.Theme.RestaurantApp"
        parent="Theme.Material3.DayNight.NoActionBar">
        <item name="android:fontFamily">@font/arial_rounded_mt_bold</item>
        <item name="fontFamily">@font/arial_rounded_mt_bold</item>
        <item name="colorPrimary">@color/primary_dark</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/primary</item>
        <item name="colorOnSecondary">@color/text_heading</item>
        <item name="colorSurface">@color/white</item>
        <item name="colorOnSurface">@color/text_heading</item>
        <item name="colorError">@color/destructive</item>
        <item name="colorOnError">@color/white</item>
        <item name="android:colorAccent">@color/primary</item>
        <item name="rolePrimary">@color/primary</item>
        <item name="rolePrimaryDark">@color/primary_dark</item>
        <item name="rolePrimaryLight">@color/primary_light</item>
        <item name="roleAccent">@color/primary_lime</item>
        <item name="roleOnPrimary">@color/white</item>
        <item name="rolePageBackground">@color/white</item>
        <item name="roleOverlayStart">@color/black_75</item>
        <item name="roleOverlayEnd">@color/black_45</item>
    </style>
    <style
        name="Theme.RestaurantApp"
        parent="Base.Theme.RestaurantApp" />
    <style
        name="Theme.RestaurantApp.Admin"
        parent="Base.Theme.RestaurantApp">
        <item name="colorPrimary">@color/admin_primary</item>
        <item name="colorSecondary">@color/admin_accent</item>
        <item name="android:colorAccent">@color/admin_primary</item>
        <item name="rolePrimary">@color/admin_primary</item>
        <item name="rolePrimaryDark">@color/admin_primary_dark</item>
        <item name="rolePrimaryLight">@color/admin_primary_light</item>
        <item name="roleAccent">@color/admin_accent</item>
        <item name="roleOnPrimary">@color/white</item>
        <item name="rolePageBackground">@color/admin_page_background</item>
        <item name="roleOverlayStart">@color/admin_overlay_start</item>
        <item name="roleOverlayEnd">@color/admin_overlay_end</item>
    </style>
    <style
        name="Theme.RestaurantApp.Personal"
        parent="Base.Theme.RestaurantApp">
        <item name="colorPrimary">@color/personal_primary_dark</item>
        <item name="colorSecondary">@color/personal_primary</item>
        <item name="android:colorAccent">@color/personal_primary</item>
        <item name="rolePrimary">@color/personal_primary</item>
        <item name="rolePrimaryDark">@color/personal_primary_dark</item>
        <item name="rolePrimaryLight">@color/personal_primary_light</item>
        <item name="roleAccent">@color/personal_accent</item>
        <item name="roleOnPrimary">@color/white</item>
        <item name="rolePageBackground">@color/personal_page_background</item>
        <item name="roleOverlayStart">@color/personal_overlay_start</item>
        <item name="roleOverlayEnd">@color/personal_overlay_end</item>
    </style>
</resources>

```

### `app/src/main/res/drawable/campus_dashboard_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="360dp"
    android:height="800dp"
    android:viewportWidth="360"
    android:viewportHeight="800">

    <path android:pathData="M0,0H360V800H0Z">
        <aapt:attr name="android:fillColor">
            <gradient
                android:endX="360"
                android:endY="800"
                android:endColor="@color/campus_background_end"
                android:startColor="@color/campus_background_start"
                android:startX="0"
                android:startY="0"
                android:type="linear" />
        </aapt:attr>
    </path>

    <path
        android:fillAlpha="0.48"
        android:fillColor="@color/campus_background_end"
        android:pathData="M115,-70H360V126C292,179 228,161 177,204C130,244 88,279 0,274V122C72,92 103,37 115,-70Z" />

    <path
        android:fillAlpha="0.9"
        android:fillColor="@color/campus_wave_cyan"
        android:pathData="M0,239C69,205 103,232 143,286C190,351 239,353 360,318V552C275,584 193,568 124,514C73,474 42,415 0,410Z" />

    <path
        android:fillAlpha="0.5"
        android:fillColor="@color/campus_background_end"
        android:pathData="M0,320C78,294 91,365 148,421C204,476 277,492 360,454V682C257,713 133,667 0,596Z" />

    <path
        android:fillAlpha="0.72"
        android:fillColor="@color/campus_wave_indigo"
        android:pathData="M0,421C49,388 93,415 126,469C171,544 250,569 360,510V800H0Z" />

    <path
        android:fillAlpha="0.88"
        android:fillColor="@color/campus_background_start"
        android:pathData="M0,506C84,469 152,535 220,592C277,641 320,655 360,628V800H0Z" />

    <path android:pathData="M360,344C318,340 287,371 287,414C287,459 320,489 360,492Z">
        <aapt:attr name="android:fillColor">
            <gradient
                android:centerX="340"
                android:centerY="404"
                android:endColor="@color/campus_wave_indigo"
                android:gradientRadius="90"
                android:startColor="@color/campus_orb_pink"
                android:type="radial" />
        </aapt:attr>
    </path>

    <path
        android:fillAlpha="0.9"
        android:fillColor="@color/campus_background_end"
        android:pathData="M0,705C75,655 150,713 224,762C275,797 320,812 360,783V800H0Z" />
</vector>

```

### `app/src/main/res/drawable/campus_action_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ripple
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/white_25">
    <item>
        <shape>
            <corners android:radius="100dp" />
            <gradient
                android:angle="0"
                android:startColor="@color/campus_button_start"
                android:endColor="@color/campus_button_end" />
            <stroke
                android:width="1dp"
                android:color="#80FFFFFF" />
        </shape>
    </item>
</ripple>

```

### `app/src/main/res/drawable/campus_avatar_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <gradient
        android:angle="315"
        android:startColor="#66FFFFFF"
        android:endColor="#338A5CFF" />
    <stroke
        android:width="1dp"
        android:color="@color/campus_glass_border" />
</shape>

```

### `app/src/main/res/drawable/campus_claim_card_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="22dp" />
    <solid android:color="@color/campus_glass" />
    <stroke
        android:width="1dp"
        android:color="@color/campus_glass_border" />
</shape>

```

### `app/src/main/res/drawable/pet_dialog_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="28dp" />
    <solid android:color="@color/white" />
    <stroke
        android:width="1dp"
        android:color="@color/search_border" />
</shape>

```

### `app/src/main/res/drawable/chat_pet_bubble.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners
        android:topLeftRadius="5dp"
        android:topRightRadius="20dp"
        android:bottomLeftRadius="20dp"
        android:bottomRightRadius="20dp" />
    <solid android:color="@color/white" />
    <stroke
        android:width="1dp"
        android:color="@color/search_border" />
</shape>

```

### `app/src/main/res/drawable/chat_user_bubble.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners
        android:topLeftRadius="20dp"
        android:topRightRadius="5dp"
        android:bottomLeftRadius="20dp"
        android:bottomRightRadius="20dp" />
    <gradient
        android:angle="315"
        android:startColor="@color/campus_button_start"
        android:endColor="@color/campus_button_end" />
</shape>

```

### `app/src/main/res/drawable/chat_composer_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="100dp" />
    <solid android:color="@color/search_background" />
    <stroke
        android:width="1dp"
        android:color="@color/search_border" />
</shape>

```

### `app/src/main/res/drawable/chat_send_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ripple
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/white_25">
    <item>
        <shape android:shape="oval">
            <gradient
                android:angle="315"
                android:startColor="@color/campus_button_start"
                android:endColor="@color/campus_button_end" />
        </shape>
    </item>
</ripple>

```

### `app/src/main/res/drawable/chat_match_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="14dp" />
    <solid android:color="#FFF5F8FF" />
    <stroke
        android:width="1dp"
        android:color="@color/personal_primary_light" />
</shape>

```

### `app/src/main/res/drawable/ic_chat_attach.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/transparent"
        android:pathData="M20,11.5L12.5,19A6,6 0,0 1,4 10.5L13,1.5A4,4 0,0 1,18.7 7.2L9.7,16.2A2,2 0,0 1,6.9 13.4L15,5.3"
        android:strokeColor="@color/personal_primary_dark"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:strokeWidth="2" />
</vector>

```

### `app/src/main/res/drawable/ic_chat_send.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/white"
        android:pathData="M3,4L22,12L3,20L6,13L15,12L6,11Z" />
</vector>

```

### `app/src/main/res/drawable/dashboard_sheet_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android">
    <corners
        android:topLeftRadius="24dp"
        android:topRightRadius="24dp" />
    <solid
        android:color="@color/white" />
</shape>

```

### `app/src/main/res/drawable/menu_search_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android">
    <corners
        android:radius="100dp" />
    <solid
        android:color="@color/search_background" />
</shape>

```

### `app/src/main/res/drawable/user_detail_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android">
    <solid
        android:color="?attr/rolePrimaryLight" />
    <corners
        android:radius="100dp" />
</shape>

```

### `app/src/main/res/drawable/ic_notification_close.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="@color/text_body"
        android:strokeWidth="2.4"
        android:strokeLineCap="round"
        android:pathData="M5,5L19,19 M19,5L5,19" />
</vector>

```

### `app/src/main/res/drawable/ic_dashboard_bell.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/white"
        android:pathData="M12,2c-0.55,0 -1,0.45 -1,1v0.08C7.56,3.56 5,6.5 5,10v4.3l-1.8,2.9c-0.4,0.65 0.07,1.5 0.83,1.5h15.94c0.76,0 1.23,-0.85 0.83,-1.5L19,14.3V10c0,-3.5 -2.56,-6.44 -6,-6.92V3c0,-0.55 -0.45,-1 -1,-1zM9.5,20h5c-0.23,1.16 -1.26,2 -2.5,2s-2.27,-0.84 -2.5,-2z" />
</vector>

```

### `app/src/main/res/drawable/ic_list_back.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="@color/text_heading"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M19,12H5 M12,5L5,12L12,19" />
</vector>

```

### `app/src/main/res/drawable/campus_claim_row_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="22dp" />
    <gradient
        android:angle="0"
        android:startColor="#3DFFFFFF"
        android:endColor="#1AFFFFFF" />
    <stroke
        android:width="1dp"
        android:color="#66AFC9FF" />
</shape>

```

### `app/src/main/res/drawable/campus_claim_image_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="18dp" />
    <gradient
        android:angle="315"
        android:startColor="#FFFFFFFF"
        android:endColor="#FFDDEAFF" />
</shape>

```

### `app/src/main/res/drawable/claim_status_pending.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="100dp" />
    <solid android:color="@color/claim_pending_background" />
</shape>

```

### `app/src/main/res/drawable/claim_status_approved.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="100dp" />
    <solid android:color="@color/claim_approved_background" />
</shape>

```

### `app/src/main/res/drawable/claim_status_rejected.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="100dp" />
    <solid android:color="@color/claim_rejected_background" />
</shape>

```

### `app/src/main/res/drawable/claim_status_returned.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <corners android:radius="100dp" />
    <solid android:color="@color/claim_returned_background" />
</shape>

```

### `app/src/main/res/drawable/ic_claim_chevron.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="20dp"
    android:height="20dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/transparent"
        android:pathData="M9,4L17,12L9,20"
        android:strokeColor="@color/white"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:strokeWidth="2.4" />
</vector>

```

### `app/src/main/res/drawable/ic_claim_location.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="18dp"
    android:height="18dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@color/campus_text_secondary"
        android:pathData="M12,2A7,7 0,0 0,5 9C5,14.25 12,22 12,22S19,14.25 19,9A7,7 0,0 0,12 2ZM12,6A3,3 0,1 1,12 12A3,3 0,1 1,12 6Z" />
</vector>

```

### `app/src/main/res/drawable/claim_wallet.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="72dp"
    android:height="72dp"
    android:viewportWidth="100"
    android:viewportHeight="100">
    <path
        android:fillColor="#151923"
        android:pathData="M14,27Q14,18 24,18H78Q86,18 86,27V75Q86,83 77,83H24Q14,83 14,73Z" />
    <path
        android:fillColor="#2B3140"
        android:pathData="M14,32H86V45H14Z" />
    <path
        android:fillColor="#090B10"
        android:pathData="M62,45H90V66H62Q54,66 54,56Q54,45 62,45Z" />
    <path
        android:fillColor="#70809A"
        android:pathData="M67,53A3,3 0,1 1,67 59A3,3 0,1 1,67 53Z" />
</vector>

```

### `app/src/main/res/drawable/claim_student_id.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="72dp"
    android:height="72dp"
    android:viewportWidth="100"
    android:viewportHeight="100">
    <path
        android:fillColor="#F7FAFF"
        android:pathData="M9,22Q9,16 15,16H85Q91,16 91,22V78Q91,84 85,84H15Q9,84 9,78Z" />
    <path
        android:fillColor="#1558D6"
        android:pathData="M9,22Q9,16 15,16H85Q91,16 91,22V35H9Z" />
    <path
        android:fillColor="#37445E"
        android:pathData="M18,45H42V72H18Z" />
    <path
        android:fillColor="#8FB0E8"
        android:pathData="M50,45H82V50H50ZM50,56H76V61H50ZM50,67H84V72H50Z" />
</vector>

```

### `app/src/main/res/drawable/claim_phone.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="72dp"
    android:height="72dp"
    android:viewportWidth="100"
    android:viewportHeight="100">
    <path
        android:fillColor="#11151D"
        android:pathData="M29,7Q22,7 22,15V85Q22,93 30,93H70Q78,93 78,85V15Q78,7 70,7Z" />
    <path
        android:fillColor="#233047"
        android:pathData="M27,18H73V79H27Z" />
    <path
        android:fillColor="#3E8DFF"
        android:pathData="M28,19H72V43L28,69Z" />
    <path
        android:fillColor="#92BFFF"
        android:pathData="M44,12H56V14H44ZM47,84A3,3 0,1 1,53 84A3,3 0,1 1,47 84Z" />
</vector>

```

### `app/src/main/res/drawable/claim_keys.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="72dp"
    android:height="72dp"
    android:viewportWidth="100"
    android:viewportHeight="100">
    <path
        android:fillColor="@android:color/transparent"
        android:pathData="M36,18A18,18 0,1 1,36 54A18,18 0,1 1,36 18Z"
        android:strokeColor="#C8CCD3"
        android:strokeWidth="8" />
    <path
        android:fillColor="#D7AA45"
        android:pathData="M48,47L82,81L73,90L39,56Z" />
    <path
        android:fillColor="#D7AA45"
        android:pathData="M67,72L76,63L83,70L74,79ZM76,81L85,72L92,79L83,88Z" />
    <path
        android:fillColor="#313745"
        android:pathData="M32,35A7,7 0,1 1,46 35A7,7 0,1 1,32 35Z" />
</vector>

```

### `app/src/main/res/drawable/claim_bottle.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="72dp"
    android:height="72dp"
    android:viewportWidth="100"
    android:viewportHeight="100">
    <path
        android:fillColor="#102D5E"
        android:pathData="M40,7H60V18H40Z" />
    <path
        android:fillColor="#1773E6"
        android:pathData="M36,17Q31,23 31,32V84Q31,92 39,92H61Q69,92 69,84V32Q69,23 64,17Z" />
    <path
        android:fillColor="#3E9AFF"
        android:pathData="M38,26H47V83H38Z" />
    <path
        android:fillColor="#0D4FAE"
        android:pathData="M34,48H66V57H34Z" />
</vector>

```

### `app/src/main/res/layout/activity_curve_test.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/campus_dashboard_background"
    android:orientation="vertical"
    android:paddingHorizontal="22dp"
    android:paddingTop="18dp"
    android:paddingBottom="28dp"
    tools:context=".CurveTestActivity">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="54dp"
        android:gravity="center_vertical"
        android:orientation="horizontal">

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="horizontal">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Campus"
                android:textColor="@color/white"
                android:textSize="@dimen/type_display" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Find"
                android:textColor="@color/campus_wave_cyan"
                android:textSize="@dimen/type_display" />
        </LinearLayout>

        <androidx.appcompat.widget.AppCompatImageButton
            android:layout_width="42dp"
            android:layout_height="42dp"
            android:background="@android:color/transparent"
            android:contentDescription="Notifications"
            android:padding="9dp"
            android:src="@drawable/ic_dashboard_bell"
            app:tint="@color/white" />

        <TextView
            android:layout_width="46dp"
            android:layout_height="46dp"
            android:layout_marginStart="10dp"
            android:background="@drawable/campus_avatar_background"
            android:gravity="center"
            android:text="MJ"
            android:textColor="@color/white"
            android:textSize="@dimen/type_body_large" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="144dp"
        android:layout_marginTop="18dp"
        android:gravity="center_vertical"
        android:orientation="horizontal">

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Good afternoon,"
                android:textColor="@color/white"
                android:textSize="@dimen/type_large_heading" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Maria!"
                android:textColor="@color/white"
                android:textSize="@dimen/type_report_total" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="4dp"
                android:text="22 October 2026"
                android:textColor="@color/campus_text_secondary"
                android:textSize="@dimen/type_body_large" />
        </LinearLayout>

        <LinearLayout
            android:layout_width="104dp"
            android:layout_height="96dp"
            android:background="@drawable/campus_claim_card_background"
            android:gravity="center"
            android:orientation="vertical">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="16"
                android:textColor="@color/white"
                android:textSize="@dimen/type_metric" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="3dp"
                android:text="claims"
                android:textColor="@color/campus_text_secondary"
                android:textSize="@dimen/type_body" />
        </LinearLayout>
    </LinearLayout>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <ImageView
            android:id="@+id/campus_helper_pet"
            android:layout_width="350dp"
            android:layout_height="350dp"
            android:layout_gravity="bottom|center_horizontal"
            android:contentDescription="Campus lost and found helper"
            android:scaleType="fitCenter"
            android:src="@drawable/campus_helper_pet" />

        <FrameLayout
            android:layout_width="260dp"
            android:layout_height="112dp"
            android:layout_gravity="top|end"
            android:layout_marginTop="8dp"
            android:layout_marginEnd="12dp">

            <View
                android:layout_width="22dp"
                android:layout_height="22dp"
                android:layout_gravity="bottom|start"
                android:layout_marginStart="44dp"
                android:layout_marginBottom="10dp"
                android:background="@color/white"
                android:rotation="45" />

            <ViewFlipper
                android:id="@+id/pet_dialog"
                android:layout_width="match_parent"
                android:layout_height="94dp"
                android:autoStart="true"
                android:flipInterval="2400"
                android:inAnimation="@android:anim/fade_in"
                android:outAnimation="@android:anim/fade_out">

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@drawable/pet_dialog_background"
                    android:gravity="center"
                    android:padding="14dp"
                    android:text="Lost something?&#10;I can help you find it!"
                    android:textAlignment="center"
                    android:textColor="@color/campus_speech_text"
                    android:textSize="@dimen/type_body_large" />

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@drawable/pet_dialog_background"
                    android:gravity="center"
                    android:padding="14dp"
                    android:text="Check found items&#10;before reporting."
                    android:textAlignment="center"
                    android:textColor="@color/campus_speech_text"
                    android:textSize="@dimen/type_body_large" />

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@drawable/pet_dialog_background"
                    android:gravity="center"
                    android:padding="14dp"
                    android:text="Still missing?&#10;Create a lost-item report."
                    android:textAlignment="center"
                    android:textColor="@color/campus_speech_text"
                    android:textSize="@dimen/type_body_large" />
            </ViewFlipper>
        </FrameLayout>
    </FrameLayout>

    <androidx.appcompat.widget.AppCompatButton
        android:id="@+id/find_item_button"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_marginTop="18dp"
        android:background="@drawable/campus_action_background"
        android:text="Find My Item   →"
        android:textAllCaps="false"
        android:textColor="@color/white"
        android:textSize="@dimen/type_button" />
</LinearLayout>

```

### `app/src/main/res/layout/bottom_sheet_find_item.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/dashboard_sheet_background"
    android:orientation="vertical"
    android:paddingHorizontal="16dp"
    android:paddingBottom="16dp">

    <View
        android:layout_width="38dp"
        android:layout_height="4dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="10dp"
        android:background="@drawable/menu_search_background"
        android:backgroundTint="@color/personal_primary_light" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:layout_marginTop="8dp"
        android:gravity="center_vertical"
        android:orientation="horizontal">

        <ImageView
            android:layout_width="46dp"
            android:layout_height="46dp"
            android:contentDescription="CampusFind helper"
            android:scaleType="fitCenter"
            android:src="@drawable/campus_helper_pet" />

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="10dp"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Find My Item"
                android:textColor="@color/text_heading"
                android:textSize="@dimen/type_page_heading" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="2dp"
                android:text="Chat with your CampusFind helper"
                android:textColor="@color/text_subheading"
                android:textSize="@dimen/type_subheading" />
        </LinearLayout>

        <androidx.appcompat.widget.AppCompatImageButton
            android:id="@+id/find_chat_close"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:background="@drawable/user_detail_background"
            android:contentDescription="Close"
            android:padding="10dp"
            android:src="@drawable/ic_notification_close" />
    </LinearLayout>

    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:background="@color/page_divider" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/find_chat_list"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingTop="10dp"
        android:paddingBottom="12dp"
        tools:itemCount="5"
        tools:listitem="@layout/item_find_chat_message" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="58dp"
        android:background="@drawable/chat_composer_background"
        android:gravity="center_vertical"
        android:orientation="horizontal"
        android:paddingHorizontal="6dp">

        <androidx.appcompat.widget.AppCompatImageButton
            android:id="@+id/find_chat_attach"
            android:layout_width="44dp"
            android:layout_height="44dp"
            android:background="@android:color/transparent"
            android:contentDescription="Attach photos"
            android:padding="10dp"
            android:src="@drawable/ic_chat_attach"
            android:visibility="gone" />

        <EditText
            android:id="@+id/find_chat_input"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:background="@android:color/transparent"
            android:hint="Type your answer…"
            android:imeOptions="actionSend"
            android:inputType="textCapSentences"
            android:maxLines="3"
            android:paddingHorizontal="12dp"
            android:textColor="@color/text_heading"
            android:textColorHint="@color/search_hint"
            android:textSize="@dimen/type_body" />

        <androidx.appcompat.widget.AppCompatImageButton
            android:id="@+id/find_chat_send"
            android:layout_width="44dp"
            android:layout_height="44dp"
            android:background="@drawable/chat_send_background"
            android:contentDescription="Send"
            android:padding="11dp"
            android:src="@drawable/ic_chat_send" />
    </LinearLayout>
</LinearLayout>

```

### `app/src/main/res/layout/item_find_chat_message.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingVertical="5dp">

    <LinearLayout
        android:id="@+id/chat_pet_row"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="top"
        android:orientation="horizontal">

        <ImageView
            android:layout_width="36dp"
            android:layout_height="36dp"
            android:contentDescription="CampusFind helper"
            android:scaleType="fitCenter"
            android:src="@drawable/campus_helper_pet" />

        <TextView
            android:id="@+id/chat_pet_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:background="@drawable/chat_pet_bubble"
            android:maxWidth="270dp"
            android:paddingHorizontal="15dp"
            android:paddingVertical="11dp"
            android:text="Did you lose an item?"
            android:textColor="@color/text_heading"
            android:textSize="@dimen/type_body" />
    </LinearLayout>

    <TextView
        android:id="@+id/chat_user_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="end"
        android:background="@drawable/chat_user_bubble"
        android:maxWidth="280dp"
        android:paddingHorizontal="16dp"
        android:paddingVertical="11dp"
        android:text="Yes"
        android:textColor="@color/white"
        android:textSize="@dimen/type_body"
        android:visibility="gone" />

    <LinearLayout
        android:id="@+id/chat_matches_row"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="top"
        android:orientation="horizontal"
        android:visibility="gone">

        <ImageView
            android:layout_width="36dp"
            android:layout_height="36dp"
            android:contentDescription="CampusFind helper"
            android:scaleType="fitCenter"
            android:src="@drawable/campus_helper_pet" />

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:layout_weight="1"
            android:background="@drawable/chat_pet_bubble"
            android:orientation="vertical"
            android:padding="12dp">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="I found 2 possible matches."
                android:textColor="@color/text_heading"
                android:textSize="@dimen/type_body" />

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="10dp"
                android:background="@drawable/chat_match_background"
                android:gravity="center_vertical"
                android:orientation="horizontal"
                android:padding="10dp">

                <ImageView
                    android:layout_width="48dp"
                    android:layout_height="48dp"
                    android:contentDescription="Black wallet"
                    android:src="@drawable/claim_wallet" />

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="10dp"
                    android:layout_weight="1"
                    android:orientation="vertical">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Black Wallet"
                        android:textColor="@color/text_heading"
                        android:textSize="@dimen/type_label" />

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="3dp"
                        android:text="Cafeteria · Oct 22"
                        android:textColor="@color/text_subheading"
                        android:textSize="@dimen/type_caption" />
                </LinearLayout>
            </LinearLayout>

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="7dp"
                android:background="@drawable/chat_match_background"
                android:gravity="center_vertical"
                android:orientation="horizontal"
                android:padding="10dp">

                <ImageView
                    android:layout_width="48dp"
                    android:layout_height="48dp"
                    android:contentDescription="Checkered wallet"
                    android:src="@drawable/claim_wallet" />

                <LinearLayout
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="10dp"
                    android:layout_weight="1"
                    android:orientation="vertical">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Checkered Wallet"
                        android:textColor="@color/text_heading"
                        android:textSize="@dimen/type_label" />

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="3dp"
                        android:text="Cafeteria · Oct 21"
                        android:textColor="@color/text_subheading"
                        android:textSize="@dimen/type_caption" />
                </LinearLayout>
            </LinearLayout>
        </LinearLayout>
    </LinearLayout>
</FrameLayout>

```

### `app/src/main/res/layout/activity_claims_test.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/campus_dashboard_background"
    android:orientation="vertical"
    android:paddingHorizontal="18dp"
    android:paddingTop="16dp"
    tools:context=".ClaimsTestActivity">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:gravity="center_vertical"
        android:orientation="horizontal">

        <androidx.appcompat.widget.AppCompatImageButton
            android:id="@+id/claims_back"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:background="@android:color/transparent"
            android:contentDescription="Back"
            android:padding="8dp"
            android:src="@drawable/ic_list_back"
            app:tint="@color/white" />

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:layout_weight="1"
            android:orientation="horizontal">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Campus"
                android:textColor="@color/white"
                android:textSize="@dimen/type_display" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Find"
                android:textColor="@color/campus_wave_cyan"
                android:textSize="@dimen/type_display" />
        </LinearLayout>

        <androidx.appcompat.widget.AppCompatImageButton
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:background="@android:color/transparent"
            android:contentDescription="Notifications"
            android:padding="9dp"
            android:src="@drawable/ic_dashboard_bell"
            app:tint="@color/white" />

        <TextView
            android:layout_width="44dp"
            android:layout_height="44dp"
            android:layout_marginStart="8dp"
            android:background="@drawable/campus_avatar_background"
            android:gravity="center"
            android:text="MJ"
            android:textColor="@color/white"
            android:textSize="@dimen/type_body_large" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="174dp"
        android:layout_marginTop="10dp"
        android:gravity="center_vertical"
        android:orientation="horizontal">

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="My Claims"
                android:textColor="@color/white"
                android:textSize="@dimen/type_report_total" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="Track the items you’ve claimed."
                android:textColor="@color/campus_text_secondary"
                android:textSize="@dimen/type_body_large" />
        </LinearLayout>

        <LinearLayout
            android:layout_width="104dp"
            android:layout_height="96dp"
            android:background="@drawable/campus_claim_card_background"
            android:gravity="center"
            android:orientation="vertical">

            <TextView
                android:id="@+id/claims_count"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="0"
                android:textColor="@color/white"
                android:textSize="@dimen/type_metric" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="3dp"
                android:text="claims"
                android:textColor="@color/campus_text_secondary"
                android:textSize="@dimen/type_body" />
        </LinearLayout>
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/claims_list"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingBottom="18dp"
        tools:itemCount="5"
        tools:listitem="@layout/item_claim_test" />
</LinearLayout>

```

### `app/src/main/res/layout/item_claim_test.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="124dp"
    android:layout_marginBottom="10dp"
    android:background="@drawable/campus_claim_row_background"
    android:gravity="center_vertical"
    android:orientation="horizontal"
    android:padding="10dp">

    <FrameLayout
        android:layout_width="76dp"
        android:layout_height="92dp"
        android:background="@drawable/campus_claim_image_background">

        <ImageView
            android:id="@+id/claim_image"
            android:layout_width="64dp"
            android:layout_height="64dp"
            android:layout_gravity="center"
            android:contentDescription="Claimed item"
            android:scaleType="fitCenter"
            android:src="@drawable/claim_wallet" />
    </FrameLayout>

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="10dp"
        android:layout_weight="1"
        android:orientation="vertical">

        <TextView
            android:id="@+id/claim_title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:ellipsize="end"
            android:maxLines="1"
            android:text="Black Wallet"
            android:textColor="@color/white"
            android:textSize="@dimen/type_card_heading" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:gravity="center_vertical"
            android:orientation="horizontal">

            <ImageView
                android:layout_width="17dp"
                android:layout_height="17dp"
                android:contentDescription="Location"
                android:src="@drawable/ic_claim_location" />

            <TextView
                android:id="@+id/claim_location"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginStart="6dp"
                android:layout_weight="1"
                android:ellipsize="end"
                android:maxLines="1"
                android:text="Cafeteria"
                android:textColor="@color/campus_text_secondary"
                android:textSize="@dimen/type_subheading" />
        </LinearLayout>

        <TextView
            android:id="@+id/claim_detail"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="7dp"
            android:ellipsize="end"
            android:maxLines="1"
            android:text="Claim submitted Oct 22, 2026"
            android:textColor="@color/campus_text_secondary"
            android:textSize="@dimen/type_detail" />
    </LinearLayout>

    <TextView
        android:id="@+id/claim_status"
        android:layout_width="90dp"
        android:layout_height="40dp"
        android:layout_marginStart="8dp"
        android:background="@drawable/claim_status_pending"
        android:gravity="center"
        android:text="◷  Pending"
        android:textColor="@color/claim_pending_text"
        android:textSize="@dimen/type_label" />

    <ImageView
        android:layout_width="22dp"
        android:layout_height="40dp"
        android:layout_marginStart="4dp"
        android:contentDescription="Open claim"
        android:padding="3dp"
        android:src="@drawable/ic_claim_chevron" />
</LinearLayout>

```

### `app/src/main/java/com/example/restaurantapp/CurveTestActivity.java`

```java
package com.example.restaurantapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.restaurantapp.adapters.FindChatAdapter;
import com.example.restaurantapp.databinding.ActivityCurveTestBinding;
import com.example.restaurantapp.databinding.BottomSheetFindItemBinding;
import com.example.restaurantapp.helpers.AppToast;
import com.example.restaurantapp.models.FindChatMessage;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CurveTestActivity extends AppCompatActivity {
    private final ArrayList<FindChatMessage> messages = new ArrayList<>();
    private BottomSheetFindItemBinding chatBinding;
    private FindChatAdapter chatAdapter;
    private int step;
    private int requiredPhotos = 2;
    private int photoCount;
    private String itemName, description, location, lossDate;

    private final ActivityResultLauncher<String> photoPicker = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(), photos -> {
                boolean invalid = requiredPhotos == 3 ? photos.size() != 3
                        : photos.size() < 2 || photos.size() > 3;
                if (invalid) {
                    String amount = requiredPhotos == 3 ? "exactly 3 photos" : "2–3 photos";
                    AppToast.error(this, "Select " + amount, "Please choose " + amount + " of your item.");
                    return;
                }
                addUser(photos.size() + " photos attached");
                photoCount = photos.size();
                chatBinding.findChatAttach.setVisibility(View.GONE);
                showReportSummary();
            });

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(getColor(R.color.campus_background_start));
        getWindow().setNavigationBarColor(getColor(R.color.campus_background_start));
        getWindow().getDecorView().setSystemUiVisibility(0);
        ActivityCurveTestBinding binding = ActivityCurveTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ObjectAnimator jump = ObjectAnimator.ofFloat(binding.campusHelperPet, "translationY",
                0, -28 * getResources().getDisplayMetrics().density, 0, 0, 0);
        jump.setDuration(2000);
        jump.setRepeatCount(ValueAnimator.INFINITE);
        jump.start();
        binding.findItemButton.setOnClickListener(v -> openFindChat());
    }

    private void openFindChat() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        chatBinding = BottomSheetFindItemBinding.inflate(getLayoutInflater());
        sheet.setContentView(chatBinding.getRoot());
        messages.clear();
        step = 0;
        requiredPhotos = 2;
        itemName = "";
        description = "";
        location = "";
        lossDate = "";
        photoCount = 0;
        chatAdapter = new FindChatAdapter(this, messages);
        chatBinding.findChatList.setLayoutManager(new LinearLayoutManager(this));
        chatBinding.findChatList.setAdapter(chatAdapter);
        chatBinding.findChatClose.setOnClickListener(v -> sheet.dismiss());
        chatBinding.findChatSend.setOnClickListener(v -> sendAnswer());
        chatBinding.findChatInput.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_SEND) {
                sendAnswer();
                return true;
            }
            return false;
        });
        chatBinding.findChatAttach.setOnClickListener(v -> photoPicker.launch("image/*"));
        addPet("Did you lose an item?");
        chatBinding.findChatInput.setHint("Reply Yes to begin…");
        sheet.show();
        View panel = sheet.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        panel.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * .9f);
        panel.requestLayout();
        sheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void sendAnswer() {
        String answer = chatBinding.findChatInput.getText().toString().trim();
        if (answer.isEmpty() || step == 5 || step == 6) return;
        chatBinding.findChatInput.setText("");
        addUser(answer);
        if (step == 8) {
            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                addPet("Your lost report has been submitted. I hope you find your item soon.");
                step = 6;
                finishConversation("Lost report submitted");
            } else if (answer.equalsIgnoreCase("no") || answer.toLowerCase(Locale.ROOT).contains("not")
                    || answer.equalsIgnoreCase("cancel")) {
                addPet("Your report was not submitted.");
                step = 6;
                finishConversation("Report cancelled");
            } else {
                addPet("Please reply Yes to submit the report or No to cancel.");
            }
        } else if (step == 7) {
            if (answer.toLowerCase(Locale.ROOT).contains("not")) {
                requiredPhotos = 3;
                addPet("Please send exactly 3 clear photos of your item so I can create a lost report.");
                step = 5;
                chatBinding.findChatInput.setEnabled(false);
                chatBinding.findChatInput.setHint("Attach exactly 3 photos");
                chatBinding.findChatAttach.setVisibility(View.VISIBLE);
                chatBinding.findChatSend.setVisibility(View.GONE);
            } else {
                addPet("Great! Open the matching item to continue your claim.");
                step = 6;
                chatBinding.findChatInput.setEnabled(false);
                chatBinding.findChatInput.setHint("Match selected");
                chatBinding.findChatSend.setEnabled(false);
            }
        } else if (step == 0) {
            if (!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("y")) {
                addPet("No problem. Reply Yes whenever you need help finding something.");
                return;
            }
            step = 1;
            addPet("What is the name of the item you lost?");
            chatBinding.findChatInput.setHint("Example: Wallet");
        } else if (step == 1) {
            itemName = answer;
            step = 2;
            addPet("Could you please describe it?");
            chatBinding.findChatInput.setHint("Describe color, pattern, or marks…");
        } else if (step == 2) {
            description = answer;
            step = 3;
            addPet("Where did you lose it?");
            chatBinding.findChatInput.setHint("Example: Cafeteria");
        } else if (step == 3) {
            location = answer;
            step = 4;
            addPet("When did you lose it?");
            chatBinding.findChatInput.setHint("Select a date…");
            showDatePicker();
        } else {
            lossDate = answer;
            showResults();
        }
    }

    private void showDatePicker() {
        Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, (picker, year, month, day) -> {
            date.set(year, month, day);
            lossDate = new SimpleDateFormat("MMM d, yyyy", Locale.US).format(date.getTime());
            addUser(lossDate);
            showResults();
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showResults() {
        boolean matched = itemName.toLowerCase(Locale.ROOT).contains("wallet")
                && location.toLowerCase(Locale.ROOT).contains("cafeteria");
        if (matched) {
            addMessage(new FindChatMessage(FindChatMessage.MATCHES, ""));
            addPet("Is one of these yours? Reply not if neither item matches.");
            step = 7;
            chatBinding.findChatInput.setHint("Reply not if neither matches…");
        } else {
            addPet("I couldn’t find a possible match yet.");
            addPet("Please attach at least 2 photos of your item, with a maximum of 3.");
            step = 5;
            chatBinding.findChatInput.setEnabled(false);
            chatBinding.findChatInput.setHint("Attach 2–3 photos");
            chatBinding.findChatAttach.setVisibility(View.VISIBLE);
            chatBinding.findChatSend.setVisibility(View.GONE);
        }
    }

    private void addPet(String text) {
        addMessage(new FindChatMessage(FindChatMessage.PET, text));
    }

    private void showReportSummary() {
        addPet("Please review your lost report:\n\nItem: " + itemName
                + "\nDescription: " + description
                + "\nLocation: " + location
                + "\nDate lost: " + lossDate
                + "\nPhotos: " + photoCount);
        addPet("Submit this lost report? Reply Yes to submit or No to cancel.");
        step = 8;
        chatBinding.findChatInput.setEnabled(true);
        chatBinding.findChatInput.setHint("Reply Yes or No…");
        chatBinding.findChatSend.setVisibility(View.VISIBLE);
        chatBinding.findChatSend.setEnabled(true);
    }

    private void finishConversation(String hint) {
        chatBinding.findChatInput.setEnabled(false);
        chatBinding.findChatInput.setHint(hint);
        chatBinding.findChatSend.setEnabled(false);
    }

    private void addUser(String text) {
        addMessage(new FindChatMessage(FindChatMessage.USER, text));
    }

    private void addMessage(FindChatMessage message) {
        messages.add(message);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatBinding.findChatList.post(() -> chatBinding.findChatList.smoothScrollToPosition(messages.size() - 1));
    }
}

```

### `app/src/main/java/com/example/restaurantapp/ClaimsTestActivity.java`

```java
package com.example.restaurantapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.restaurantapp.adapters.ClaimTestAdapter;
import com.example.restaurantapp.databinding.ActivityClaimsTestBinding;
import com.example.restaurantapp.models.ClaimTestItem;
import java.util.ArrayList;

public class ClaimsTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(getColor(R.color.campus_background_start));
        getWindow().setNavigationBarColor(getColor(R.color.campus_background_start));
        getWindow().getDecorView().setSystemUiVisibility(0);
        ActivityClaimsTestBinding binding = ActivityClaimsTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<ClaimTestItem> claims = new ArrayList<>();
        claims.add(new ClaimTestItem("Black Wallet", "Cafeteria", "Claim submitted Oct 22, 2026", "Pending", R.drawable.claim_wallet));
        claims.add(new ClaimTestItem("Student ID", "Library", "Claim approved Oct 20, 2026", "Approved", R.drawable.claim_student_id));
        claims.add(new ClaimTestItem("Mobile Phone", "Learning Hub", "Claim rejected Oct 15, 2026", "Rejected", R.drawable.claim_phone));
        claims.add(new ClaimTestItem("Keys", "Gym", "Claim returned Oct 10, 2026", "Returned", R.drawable.claim_keys));
        claims.add(new ClaimTestItem("Water Bottle", "Cafeteria", "Claim submitted Oct 5, 2026", "Pending", R.drawable.claim_bottle));
        claims.add(new ClaimTestItem("Black Wallet", "Student Center", "Claim approved Oct 3, 2026", "Approved", R.drawable.claim_wallet));
        claims.add(new ClaimTestItem("Mobile Phone", "Science Hall", "Claim submitted Sep 29, 2026", "Pending", R.drawable.claim_phone));
        claims.add(new ClaimTestItem("Student ID", "Registrar", "Claim returned Sep 26, 2026", "Returned", R.drawable.claim_student_id));
        claims.add(new ClaimTestItem("Keys", "Parking Area", "Claim rejected Sep 22, 2026", "Rejected", R.drawable.claim_keys));
        claims.add(new ClaimTestItem("Water Bottle", "Main Building", "Claim approved Sep 18, 2026", "Approved", R.drawable.claim_bottle));
        claims.add(new ClaimTestItem("Black Wallet", "Cafeteria", "Claim returned Sep 15, 2026", "Returned", R.drawable.claim_wallet));
        claims.add(new ClaimTestItem("Mobile Phone", "Library", "Claim submitted Sep 12, 2026", "Pending", R.drawable.claim_phone));
        claims.add(new ClaimTestItem("Student ID", "Learning Hub", "Claim approved Sep 9, 2026", "Approved", R.drawable.claim_student_id));
        claims.add(new ClaimTestItem("Keys", "Gym", "Claim returned Sep 6, 2026", "Returned", R.drawable.claim_keys));
        claims.add(new ClaimTestItem("Water Bottle", "Cafeteria", "Claim rejected Sep 3, 2026", "Rejected", R.drawable.claim_bottle));
        claims.add(new ClaimTestItem("Black Wallet", "Admin Office", "Claim submitted Sep 1, 2026", "Pending", R.drawable.claim_wallet));

        binding.claimsCount.setText(String.valueOf(claims.size()));
        binding.claimsList.setLayoutManager(new LinearLayoutManager(this));
        binding.claimsList.setAdapter(new ClaimTestAdapter(this, claims));
        binding.claimsBack.setOnClickListener(v -> finish());
    }
}

```

### `app/src/main/java/com/example/restaurantapp/adapters/FindChatAdapter.java`

```java
package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.databinding.ItemFindChatMessageBinding;
import com.example.restaurantapp.models.FindChatMessage;
import java.util.ArrayList;

public class FindChatAdapter extends RecyclerView.Adapter<FindChatAdapter.Holder> {
    private final Context context;
    private final ArrayList<FindChatMessage> list;

    public FindChatAdapter(Context context, ArrayList<FindChatMessage> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemFindChatMessageBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        FindChatMessage message = list.get(position);
        holder.binding.chatPetRow.setVisibility(message.type == FindChatMessage.PET ? View.VISIBLE : View.GONE);
        holder.binding.chatUserText.setVisibility(message.type == FindChatMessage.USER ? View.VISIBLE : View.GONE);
        holder.binding.chatMatchesRow.setVisibility(message.type == FindChatMessage.MATCHES ? View.VISIBLE : View.GONE);
        holder.binding.chatPetText.setText(message.text);
        holder.binding.chatUserText.setText(message.text);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ItemFindChatMessageBinding binding;

        public Holder(ItemFindChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

```

### `app/src/main/java/com/example/restaurantapp/adapters/ClaimTestAdapter.java`

```java
package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.R;
import com.example.restaurantapp.databinding.ItemClaimTestBinding;
import com.example.restaurantapp.models.ClaimTestItem;
import java.util.ArrayList;

public class ClaimTestAdapter extends RecyclerView.Adapter<ClaimTestAdapter.Holder> {
    private final Context context;
    private final ArrayList<ClaimTestItem> list;

    public ClaimTestAdapter(Context context, ArrayList<ClaimTestItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemClaimTestBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ClaimTestItem item = list.get(position);
        holder.binding.claimImage.setImageResource(item.image);
        holder.binding.claimTitle.setText(item.title);
        holder.binding.claimLocation.setText(item.location);
        holder.binding.claimDetail.setText(item.detail);
        int background = R.drawable.claim_status_pending;
        int color = R.color.claim_pending_text;
        String status = "◷  Pending";
        if ("Approved".equals(item.status)) {
            background = R.drawable.claim_status_approved;
            color = R.color.claim_approved_text;
            status = "✓  Approved";
        } else if ("Rejected".equals(item.status)) {
            background = R.drawable.claim_status_rejected;
            color = R.color.claim_rejected_text;
            status = "×  Rejected";
        } else if ("Returned".equals(item.status)) {
            background = R.drawable.claim_status_returned;
            color = R.color.claim_returned_text;
            status = "↶  Returned";
        }
        holder.binding.claimStatus.setBackgroundResource(background);
        holder.binding.claimStatus.setTextColor(ContextCompat.getColor(context, color));
        holder.binding.claimStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ItemClaimTestBinding binding;

        public Holder(ItemClaimTestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

```

### `app/src/main/java/com/example/restaurantapp/models/FindChatMessage.java`

```java
package com.example.restaurantapp.models;

public class FindChatMessage {
    public static final int PET = 0, USER = 1, MATCHES = 2;
    public int type;
    public String text;

    public FindChatMessage(int type, String text) {
        this.type = type;
        this.text = text;
    }
}

```

### `app/src/main/java/com/example/restaurantapp/models/ClaimTestItem.java`

```java
package com.example.restaurantapp.models;

public class ClaimTestItem {
    public String title, location, detail, status;
    public int image;

    public ClaimTestItem(String title, String location, String detail, String status, int image) {
        this.title = title;
        this.location = location;
        this.detail = detail;
        this.status = status;
        this.image = image;
    }
}

```

### `app/src/main/java/com/example/restaurantapp/helpers/AppToast.java`

```java
package com.example.restaurantapp.helpers;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.restaurantapp.R;

public final class AppToast {
    public static void success(Activity activity, String title, String message) {
        show(activity, title, message, false);
    }

    public static void error(Activity activity, String title, String message) {
        show(activity, title, message, true);
    }

    private static void show(Activity activity, String title, String message, boolean error) {
        FrameLayout root = activity.findViewById(android.R.id.content);
        View previous = root.findViewWithTag(AppToast.class);
        if (previous != null) { previous.animate().cancel(); root.removeView(previous); }
        View banner = activity.getLayoutInflater().inflate(R.layout.toast_banner, root, false);
        banner.setTag(AppToast.class);
        banner.setBackgroundResource(error ? R.drawable.danger_card : R.drawable.modal_card);
        TextView icon = banner.findViewById(R.id.toast_icon);
        icon.setText(error ? "!" : "✓");
        icon.setBackgroundResource(error ? R.drawable.status_danger_circle : R.drawable.status_success_circle);
        ((TextView) banner.findViewById(R.id.toast_title)).setText(title);
        ((TextView) banner.findViewById(R.id.toast_message)).setText(message);
        int gap = Math.round(12 * activity.getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2, Gravity.TOP);
        params.setMargins(gap, gap, gap, 0);
        ViewCompat.setOnApplyWindowInsetsListener(banner, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            int[] position = new int[2];
            root.getLocationOnScreen(position);
            params.setMargins(gap + bars.left, gap + Math.max(0, bars.top - position[1]), gap + bars.right, 0);
            banner.setLayoutParams(params);
            return insets;
        });
        root.addView(banner, params);
        ViewCompat.requestApplyInsets(banner);
        banner.setTranslationY(-gap * 4);
        banner.setAlpha(0);
        banner.animate().translationY(0).alpha(1).setDuration(300).start();
        banner.setOnClickListener(v -> root.removeView(banner));
        banner.postDelayed(() -> banner.animate().alpha(0).translationY(-gap * 2).setDuration(200)
                .withEndAction(() -> root.removeView(banner)).start(), 4000);
    }
}

```

### `app/src/main/res/layout/toast_banner.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center_vertical"
    android:padding="16dp"
    android:elevation="8dp"
    android:accessibilityLiveRegion="polite"
    tools:background="@drawable/modal_card">
    <TextView
        android:id="@+id/toast_icon"
        android:layout_width="56dp"
        android:layout_height="56dp"
        android:gravity="center"
        android:includeFontPadding="false"
        android:textSize="@dimen/type_large_heading"
        android:textStyle="bold"
        android:textColor="@color/white"
        tools:background="@drawable/status_success_circle"
        tools:text="✓" />
    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:layout_marginStart="12dp"
        android:orientation="vertical">
        <TextView
            android:id="@+id/toast_title"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="@dimen/type_body_large"
            android:textStyle="bold"
            android:textColor="@color/text_heading"
            tools:text="Changes saved" />
        <TextView
            android:id="@+id/toast_message"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:textSize="@dimen/type_body"
            android:textColor="@color/text_body"
            tools:text="Your changes have been saved successfully." />
    </LinearLayout>
</LinearLayout>

```

### `app/src/main/res/drawable/danger_card.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android">
    <corners
        android:radius="24dp" />
    <gradient
        android:angle="225"
        android:startColor="@color/white"
        android:centerColor="@color/white"
        android:endColor="@color/white" />
    <stroke
        android:width="1dp"
        android:color="@color/destructive" />
</shape>

```

### `app/src/main/res/drawable/modal_card.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android">
    <corners
        android:radius="24dp" />
    <gradient
        android:angle="225"
        android:startColor="?attr/rolePrimaryLight"
        android:endColor="@color/white" />
</shape>

```

### `app/src/main/res/drawable/status_danger_circle.xml`

```xml
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">

    <item>
        <shape android:shape="oval">
            <solid android:color="?attr/rolePrimaryLight"/>
        </shape>
    </item>

    <item android:left="13dp" android:top="13dp"
        android:right="13dp" android:bottom="13dp">
        <shape android:shape="oval">
            <gradient android:angle="270"
                android:startColor="@color/destructive"
                android:endColor="@color/badge_red"/>
        </shape>
    </item>

</layer-list>

```

### `app/src/main/res/drawable/status_success_circle.xml`

```xml
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">

    <item>
        <shape android:shape="oval">
            <solid android:color="?attr/rolePrimaryLight"/>
        </shape>
    </item>

    <item android:left="13dp" android:top="13dp"
        android:right="13dp" android:bottom="13dp">
        <shape android:shape="oval">
            <gradient android:angle="270"
                android:startColor="@color/badge_green"
                android:endColor="@color/success"/>
        </shape>
    </item>

</layer-list>

```

### `app/src/main/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.RestaurantApp">
        <activity
            android:name=".CurveTestActivity"
            android:theme="@style/Theme.RestaurantApp.Personal"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity
            android:name=".ClaimsTestActivity"
            android:theme="@style/Theme.RestaurantApp.Personal"
            android:exported="false" />
        <activity
            android:name=".SampleLoginActivity"
            android:exported="true">

        </activity>
        <activity
            android:name=".SampleRegisterActivity"
            android:exported="false" />
        <activity
            android:name=".SampleActivityShowActivity"
            android:theme="@style/Theme.RestaurantApp.Personal"
            android:exported="false" />
        <activity
            android:name=".SampleReportPageActivity"
            android:theme="@style/Theme.RestaurantApp.Admin"
            android:exported="false" />
        <activity
            android:name=".SampleCreatePageActivity"
            android:theme="@style/Theme.RestaurantApp.Admin"
            android:exported="false" />
        <activity
            android:name=".DashboardSampleActivity"
            android:exported="false" />
        <activity
            android:name=".PersonalDashboardActivity"
            android:theme="@style/Theme.RestaurantApp.Personal"
            android:exported="false" />
        <activity
            android:name=".PersonalProfileActivity"
            android:theme="@style/Theme.RestaurantApp.Personal"
            android:exported="false" />
        <activity
            android:name=".admin.AdminMenuListActivity"
            android:theme="@style/Theme.RestaurantApp.Admin"
            android:exported="false" />
        <activity
            android:name=".admin.AdminItemShowActivity"
            android:theme="@style/Theme.RestaurantApp.Admin"
            android:exported="false" />
        <activity
            android:name=".admin.AdminPersonListActivity"
            android:theme="@style/Theme.RestaurantApp.Admin"
            android:exported="false" />
        <activity
            android:name=".admin.AdminDashboardActivity"
            android:theme="@style/Theme.RestaurantApp.Admin"
            android:exported="false" />
        <activity
            android:name=".admin.AdminProfileActivity"
            android:theme="@style/Theme.RestaurantApp.Admin"
            android:exported="false" />
    </application>
</manifest>

```
