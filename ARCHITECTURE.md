# Architecture Review

## المرحلة الحالية

المشروع الحالي لا يحتاج إلى تغيير لغة أو إطار عمل لمجرد التغيير. HTML/CSS/JavaScript مناسبة لـ GitHub Pages، بينما Firebase وCloudflare وApps Script تؤدي أدوار backend المطلوبة.

## أولويات إعادة الهيكلة

1. تقسيم `admin.js` الكبير إلى وحدات auth وCRUD وforms وbulk-import وtemplates وrendering.
2. توحيد الوصول إلى Firestore داخل طبقة repositories.
3. إزالة ملفات fixes المؤقتة والمراجع الميتة.
4. إصلاح Service Worker ليكون مسؤولًا عن cache فقط.
5. مراجعة Firestore Rules لكل collection.
6. تحسين pagination والتحميل التدريجي للمصادر.
7. اختبار AI وDrive وBulk Import قبل تغيير البنية الخلفية.
8. بعد الاستقرار، دراسة الانتقال التدريجي إلى TypeScript.

## قرار التقنية

لا React ولا Flutter ولا Next.js حاليًا. الهدف أولًا جعل المشروع الحالي قابلًا للصيانة والاختبار، ثم تغيير التقنية فقط إذا كان هناك سبب واضح.
