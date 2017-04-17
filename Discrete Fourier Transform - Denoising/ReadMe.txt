Part2 icin:

Discrete Fourier Transform uyguladım. Fakat cok uzun sureceginden resmi kuculttum ve thread kullandım.
Ayrıca her defasında tekrar tekrar fourier almamak icin resmi save ettim. Fakat kaydedilen resmi okumak icin
load sınıfı doubleImage okumadigi icin kendim .txt'ye yazdım ve oradan okudum. Boylelikle dogru degerler okunmus oldu.
Ters Fourier Donusumu icinde thread kullandım daha hizli calismasi icin.

Mutlaka okurken txt leri kullaniniz. Daha sonra butterworth filtremi yazdim ve onuda merkeze gore shift ettim. Cunku
real ve imag kismi shift edilmemis haliydi. Daha sonra bunlarin filtrenin altında kalan siyah ise 0.25 altinda ise 0 ladim
digerlerini aynen biraktim. Bir nevi carpmis oldum.

Yaklasık tersini alma 16 17 dk.
Normal Fourier alma 11 12 dk filan suruyor.
Resimleri son durumlari ektedir ayrica.
Noktalari tutmak icin Point sinifi yazdim.

Temel mantık fourierin magnitude alindiktan sonra simetrik bir sekilde olan beyaz noktaların tam uzerinden gececek
sekilde filtreyi oturtmak daha sonra o filtre nin merkeze gore shift edip, carpilir ve tersi alinir.

Safa Emre Dulundu