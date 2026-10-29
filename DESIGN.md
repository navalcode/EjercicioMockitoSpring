# Sales testbed: observable contracts

The surface is Java source, JSON and reproducible commands. Native Markdown hierarchy and endpoint/result tables help readers find the behavior; a graphical dashboard would add unrelated scope. No fonts, scripts, decorative animation or generated app screenshots are introduced.

The teaching loop connects a request to validated inputs, repository reads, an owned transaction and a confirmed result. Stable machine error codes remain untranslated. Existing Spanish DTO properties, date representation, package names and nullable service add/remove results are retained. Missing inputs fail before a persistence write. Duplicate input IDs are rejected deliberately; they are not silently combined.

A sale snapshots product prices. Removing a product removes all its matching lines, as the original exercise case expected, and clears each back-reference. Identifier equality uses values, while entity-object ownership stays explicit. Tests assert effects and non-effects, not just that a loop ran. The whole Spring/JPA journey validates request deserialization, dependency wiring and saved relationships separately from mocked service tests.

The default demo uses fictional data in H2 memory, a loopback binding and a printed request with actual assigned IDs. Each process owns that data; restart discards it. H2 shutdown is coordinated with Hibernate. The original eight cases remain recognizable and their historical failing version remains in Git. This review preserves the coursework attribution/history and original build pins; it does not turn the sample into a stock/payment or production commerce system.
